package com.example.recommendationsystem;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    // Simple skill-to-job mapping for collaborative filtering
    private static final Map<String, List<String>> SKILL_TO_JOBS = Map.of(
            "Java", Arrays.asList("Java Developer", "Backend Developer", "Full Stack Developer"),
            "Python", Arrays.asList("Python Developer", "Data Scientist", "Machine Learning Engineer"),
            "JavaScript", Arrays.asList("Frontend Developer", "Full Stack Developer", "React Developer"),
            "SQL", Arrays.asList("Database Administrator", "Backend Developer", "Data Analyst"),
            "React", Arrays.asList("React Developer", "Frontend Developer"),
            "Spring", Arrays.asList("Java Developer", "Backend Developer"),
            "AWS", Arrays.asList("Cloud Engineer", "DevOps Engineer"),
            "Docker", Arrays.asList("DevOps Engineer", "Backend Developer")
    );

    public List<String> recommendJobs(List<String> skills) {
        List<String> recommendedJobs = new ArrayList<>();
        for (String skill : skills) {
            List<String> jobs = SKILL_TO_JOBS.getOrDefault(skill, List.of());
            recommendedJobs.addAll(jobs);
        }
        // Remove duplicates and sort
        return recommendedJobs.stream().distinct().sorted().collect(Collectors.toList());
    }
}