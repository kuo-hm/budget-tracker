package com.budget_tracker.tracker.budget_tracker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupUrlLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${serve.servlet.context-path:/}")
    private String context;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String baseUrl = "http://localhost:" + serverPort;

        log.info("\n" +
                        "========================================" + "\n" +
                        "Budget Tracker Application Started" + "\n" +
                        "Environment: {}" + "\n" +
                        "Backend API: {}" + "\n" +
                        "Swagger UI: {}/swagger-ui/index.html" + "\n" +
                        "API Docs JSON: {}/v3/api-docs" + "\n" +
                        "Auth Endpoints: {}/api/auth" + "\n" +
                        "========================================",
                activeProfile, baseUrl+context, baseUrl, baseUrl, baseUrl
        );
    }
}