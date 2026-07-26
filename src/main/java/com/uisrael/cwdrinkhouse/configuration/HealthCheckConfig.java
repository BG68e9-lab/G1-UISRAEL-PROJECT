package com.uisrael.cwdrinkhouse.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Configuration for backend service health monitoring.
 * Performs periodic health checks to detect connectivity issues.
 * Can be disabled for testing purposes.
 */
@Configuration
@ConditionalOnProperty(
    name = "app.health-check.enabled",
    havingValue = "true",
    matchIfMissing = true
)
@EnableScheduling
public class HealthCheckConfig {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckConfig.class);

    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    private final AtomicBoolean backendHealthy = new AtomicBoolean(true);
    private final AtomicReference<LocalDateTime> lastHealthCheck = new AtomicReference<>(LocalDateTime.now());
    private final AtomicReference<String> lastError = new AtomicReference<>("");

    private final WebClient webClient;

    public HealthCheckConfig(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Performs health check every 30 seconds.
     */
    @Scheduled(fixedRate = 30000) // 30 seconds
    public void performHealthCheck() {
        webClient.get()
                .uri("/health")
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(response -> {
                    if (!backendHealthy.get()) {
                        logger.info("Backend service is back online");
                        backendHealthy.set(true);
                        lastError.set("");
                    }
                    lastHealthCheck.set(LocalDateTime.now());
                })
                .doOnError(error -> {
                    if (backendHealthy.get()) {
                        logger.warn("Backend service health check failed: {}", error.getMessage());
                        backendHealthy.set(false);
                        lastError.set(error.getClass().getSimpleName() + ": " + error.getMessage());
                    }
                    lastHealthCheck.set(LocalDateTime.now());
                })
                .onErrorResume(error -> Mono.empty())
                .subscribe();
    }

    /**
     * Returns current backend health status.
     */
    @Bean
    public BackendHealthIndicator backendHealthIndicator() {
        return new BackendHealthIndicator();
    }

    /**
     * Service to check backend health status.
     */
    public class BackendHealthIndicator {
        
        public boolean isBackendHealthy() {
            return backendHealthy.get();
        }
        
        public LocalDateTime getLastHealthCheck() {
            return lastHealthCheck.get();
        }
        
        public String getLastError() {
            return lastError.get();
        }
        
        public boolean isHealthCheckStale() {
            return Duration.between(lastHealthCheck.get(), LocalDateTime.now()).toMinutes() > 2;
        }
    }
}