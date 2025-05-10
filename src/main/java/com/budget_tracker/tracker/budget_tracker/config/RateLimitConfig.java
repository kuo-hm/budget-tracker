package com.budget_tracker.tracker.budget_tracker.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.simple(100, Duration.ofMinutes(1));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
} 