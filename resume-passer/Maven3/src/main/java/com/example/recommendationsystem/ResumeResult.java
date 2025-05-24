package com.example.recommendationsystem;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;

@Entity
public class ResumeResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @ElementCollection
    private List<String> skills;

    @ElementCollection
    private List<String> recommendedJobs;

    // Constructors
    public ResumeResult() {}

    public ResumeResult(String filename, List<String> skills, List<String> recommendedJobs) {
        this.filename = filename;
        this.skills = skills;
        this.recommendedJobs = recommendedJobs;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getRecommendedJobs() {
        return recommendedJobs;
    }

    public void setRecommendedJobs(List<String> recommendedJobs) {
        this.recommendedJobs = recommendedJobs;
    }
}