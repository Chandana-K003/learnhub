package com.learnhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LearnHubApplication {

    public static void main(String[] args) {
        // Helps diagnose deployments: shows which database URL was provided (it contains no password).
        String dbUrl = System.getenv("DB_URL");
        System.out.println(dbUrl == null
                ? "DB_URL environment variable is NOT set"
                : "DB_URL = " + dbUrl.trim() + (dbUrl.equals(dbUrl.trim()) ? "" : "  (WARNING: has extra spaces)"));
        SpringApplication.run(LearnHubApplication.class, args);
    }
}
