package com.budget_tracker.tracker.budget_tracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI budgetTrackerOpenAPI() {
        String url = "http://localhost:" + serverPort + contextPath;

        return new OpenAPI()
                .info(new Info()
                        .title("Budget Tracker API")
                        .description("API for managing budgets, expenses, and reports")
                        .version("1.0.0"))
                .addServersItem(new Server().url(url)
                        .description("Server for profile: " + activeProfile));
    }
}
