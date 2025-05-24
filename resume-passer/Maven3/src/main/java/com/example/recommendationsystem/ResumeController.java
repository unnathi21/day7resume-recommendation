package com.example.recommendationsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final WebClient webClient;
    private final RecommendationService recommendationService;
    private final ResumeResultRepository repository;

    @Autowired
    public ResumeController(WebClient.Builder webClientBuilder, RecommendationService recommendationService, ResumeResultRepository repository) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:5897").build();
        this.recommendationService = recommendationService;
        this.repository = repository;
    }

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Map<String, Object>> processResume(@RequestParam("file") MultipartFile file) {
        return Mono.fromCallable(() -> file.getBytes())
                .map(bytes -> {
                    MultipartBodyBuilder builder = new MultipartBodyBuilder();
                    builder.part("file", new ByteArrayResource(bytes))
                            .filename(file.getOriginalFilename())
                            .contentType(MediaType.APPLICATION_OCTET_STREAM);
                    return builder.build();
                })
                .flatMap(multipartData -> webClient.post()
                        .uri("/parse-resume")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(multipartData))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}))
                .map(response -> {
                    String filename = (String) response.get("filename");
                    List<String> skills = (List<String>) response.get("skills");
                    List<String> recommendedJobs = recommendationService.recommendJobs(skills);

                    // Save to database
                    ResumeResult result = new ResumeResult(filename, skills, recommendedJobs);
                    repository.save(result);

                    return Map.of(
                            "filename", filename,
                            "skills", skills,
                            "recommended_jobs", recommendedJobs
                    );
                })
                .onErrorResume(Throwable.class, e -> {
                    Map<String, Object> errorResponse = Map.of("error", "Failed to process resume: " + e.getMessage());
                    return Mono.just(errorResponse);
                });
    }
}