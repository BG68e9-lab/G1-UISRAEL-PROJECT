package com.uisrael.cwdrinkhouse.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Error response handling and translation interceptor for WebClient.
 * Translates HTTP error responses to meaningful exceptions and user messages.
 * 
 * Requirements: 18.3
 */
public class ErrorHandlingFilter implements ExchangeFilterFunction {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingFilter.class);

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return next.exchange(request)
            .flatMap(response -> {
                if (response.statusCode().isError()) {
                    // Log the error response
                    logger.warn("Error response from backend: {} {} for request {} {}", 
                        response.statusCode().value(),
                        HttpStatus.valueOf(response.statusCode().value()).getReasonPhrase(),
                        request.method(),
                        request.url());
                    
                    // Return the response as-is for now, let services handle specific error cases
                    // Services can check status codes and translate to appropriate exceptions
                    return Mono.just(response);
                } else {
                    return Mono.just(response);
                }
            })
            .doOnError(throwable -> {
                // Log connection errors with more detail
                if (throwable instanceof java.net.ConnectException) {
                    logger.error("Connection refused to backend {} {}: Backend service may be down", 
                        request.method(), request.url());
                } else if (throwable instanceof java.util.concurrent.TimeoutException) {
                    logger.error("Request timeout to backend {} {}: Backend service is taking too long to respond", 
                        request.method(), request.url());
                } else if (throwable instanceof reactor.netty.http.client.PrematureCloseException) {
                    logger.error("Connection closed prematurely by backend {} {}: Network connectivity issue", 
                        request.method(), request.url());
                } else {
                    logger.error("Unexpected error in WebClient call to {} {}: {}", 
                        request.method(), 
                        request.url(), 
                        throwable.getMessage(), 
                        throwable);
                }
            })
            .onErrorMap(throwable -> {
                // Map low-level connection errors to more specific exceptions
                if (throwable instanceof java.net.ConnectException) {
                    return new ConnectionRefusedException("Backend service unavailable: " + throwable.getMessage());
                } else if (throwable instanceof java.util.concurrent.TimeoutException) {
                    return new ConnectionTimeoutException("Backend request timed out: " + throwable.getMessage());
                } else if (throwable instanceof reactor.netty.http.client.PrematureCloseException) {
                    return new ConnectionInterruptedException("Connection closed unexpectedly: " + throwable.getMessage());
                }
                return throwable;
            });
    }

    /**
     * Translates HTTP status codes to user-friendly error messages.
     * 
     * @param statusCode the HTTP status code
     * @return a user-friendly error message
     */
    public static String translateErrorMessage(HttpStatusCode statusCode) {
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode.value());
        return switch (httpStatus) {
            case BAD_REQUEST -> "Invalid request. Please check your input and try again.";
            case UNAUTHORIZED -> "Authentication failed. Please log in again.";
            case FORBIDDEN -> "You do not have permission to perform this action.";
            case NOT_FOUND -> "The requested resource was not found.";
            case CONFLICT -> "This operation conflicts with existing data. Please refresh and try again.";
            case UNPROCESSABLE_ENTITY -> "The provided data is invalid or violates business rules.";
            case TOO_MANY_REQUESTS -> "Too many requests. Please try again later.";
            case INTERNAL_SERVER_ERROR -> "An internal server error occurred. Please try again later.";
            case BAD_GATEWAY -> "Backend service is temporarily unavailable. Please try again.";
            case SERVICE_UNAVAILABLE -> "Backend service is currently unavailable. Please try again later.";
            case GATEWAY_TIMEOUT -> "Request timeout. The backend service took too long to respond.";
            default -> "An error occurred: " + httpStatus.getReasonPhrase();
        };
    }

    /**
     * Maps HTTP status codes to domain-specific exception types.
     * Allows for more granular error handling in controllers.
     * 
     * @param statusCode the HTTP status code
     * @return a domain exception class
     */
    public static Class<? extends Exception> mapStatusToException(HttpStatusCode statusCode) {
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode.value());
        return switch (httpStatus) {
            case BAD_REQUEST -> ValidationException.class;
            case UNAUTHORIZED -> AuthenticationException.class;
            case FORBIDDEN -> AuthorizationException.class;
            case NOT_FOUND -> EntityNotFoundException.class;
            case CONFLICT -> ConflictException.class;
            case UNPROCESSABLE_ENTITY -> BusinessRuleException.class;
            case TOO_MANY_REQUESTS -> RateLimitException.class;
            case INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT 
                    -> ExternalServiceException.class;
            default -> ExternalServiceException.class;
        };
    }

    // Custom Exception Classes

    /**
     * Thrown when a validation error occurs in the backend.
     */
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when authentication fails.
     */
    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the user lacks authorization for an action.
     */
    public static class AuthorizationException extends RuntimeException {
        public AuthorizationException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when a requested entity is not found.
     */
    public static class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when an operation conflicts with existing data.
     */
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when a business rule is violated.
     */
    public static class BusinessRuleException extends RuntimeException {
        public BusinessRuleException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the API rate limit is exceeded.
     */
    public static class RateLimitException extends RuntimeException {
        public RateLimitException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when an external service call fails.
     */
    public static class ExternalServiceException extends RuntimeException {
        public ExternalServiceException(String message) {
            super(message);
        }

        public ExternalServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Thrown when connection to backend is refused.
     */
    public static class ConnectionRefusedException extends RuntimeException {
        public ConnectionRefusedException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when connection to backend times out.
     */
    public static class ConnectionTimeoutException extends RuntimeException {
        public ConnectionTimeoutException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when connection is interrupted unexpectedly.
     */
    public static class ConnectionInterruptedException extends RuntimeException {
        public ConnectionInterruptedException(String message) {
            super(message);
        }
    }
}
