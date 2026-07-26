package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.ErrorResponse;
import com.uisrael.cwdrinkhouse.exception.ErrorMessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Example service demonstrating how to use the global exception handling system.
 * This shows the integration between WebClient calls and the ErrorMessageMapper.
 */
@Service
public class ExceptionHandlingExampleService {

    private final WebClient webClient;

    public ExceptionHandlingExampleService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Example method showing how to handle WebClient exceptions
     * and let the WebUIExceptionHandler process them.
     */
    public String performBackendOperation(String data) {
        try {
            return webClient.post()
                .uri("/example")
                .bodyValue(data)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        } catch (WebClientResponseException ex) {
            // Convert WebClient exception to our custom exception hierarchy
            // This will be caught by WebUIExceptionHandler and shown to the user
            throw ErrorMessageMapper.mapException(ex);
        } catch (Exception ex) {
            // Handle any other exceptions
            throw ErrorMessageMapper.mapException(ex);
        }
    }

    /**
     * Example method showing how to handle specific business scenarios.
     */
    public void validateBusinessRules(String input) {
        if (input == null || input.trim().isEmpty()) {
            // This will be caught by WebUIExceptionHandler and show validation message
            throw ErrorMessageMapper.mapException(
                new IllegalArgumentException("Input cannot be empty"));
        }
        
        if (input.length() > 100) {
            // This will show business rule violation message
            throw ErrorMessageMapper.mapException(
                new IllegalStateException("Input exceeds maximum length"));
        }
    }
}