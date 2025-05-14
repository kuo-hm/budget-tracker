package com.budget_tracker.tracker.budget_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final CorsProperties corsProperties;
    private final VerificationInterceptor verificationInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor, CorsProperties corsProperties, 
                    VerificationInterceptor verificationInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.corsProperties = corsProperties;
        this.verificationInterceptor = verificationInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**"); 
        registry.addInterceptor(verificationInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        
        corsProperties.getAllowedOrigins().forEach(config::addAllowedOrigin);
        
        
        config.addAllowedMethod("*");
        
        
        config.addAllowedHeader("*");
        
        
        config.setAllowCredentials(true);
        
        
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
