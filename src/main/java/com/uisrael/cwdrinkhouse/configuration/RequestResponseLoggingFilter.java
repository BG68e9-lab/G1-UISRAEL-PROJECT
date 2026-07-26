package com.uisrael.cwdrinkhouse.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Request/Response logging interceptor for WebClient.
 * Logs all outgoing requests and incoming responses with correlation IDs
 * to aid in debugging and tracing distributed requests.
 * 
 * Requirements: 18.2
 */
public class RequestResponseLoggingFilter implements ExchangeFilterFunction {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        String correlationId = generateCorrelationId();
        String timestamp = LocalDateTime.now().format(formatter);
        
        // Log outgoing request
        logger.debug("WebClient Request [{}] at {}: {} {} - Headers: {}", 
            correlationId, 
            timestamp,
            request.method(), 
            request.url(), 
            request.headers());

        return next.exchange(request)
            .doOnSuccess(response -> {
                // Log incoming response
                if (response != null) {
                    logger.debug("WebClient Response [{}]: {} {} - Headers: {}", 
                        correlationId, 
                        response.statusCode().value(),
                        response.statusCode().toString(),
                        response.headers().asHttpHeaders());
                }
            })
            .doOnError(throwable -> {
                // Log errors
                logger.error("WebClient Error [{}]: {}", correlationId, throwable.getMessage(), throwable);
            });
    }

    /**
     * Generates a unique correlation ID for request/response tracing.
     * 
     * @return a unique correlation ID
     */
    private String generateCorrelationId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
