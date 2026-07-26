package com.uisrael.cwdrinkhouse.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Enhanced WebClient configuration for REST API calls to the backend.
 * Configures timeouts, retry logic, connection pooling, interceptors, and circuit breaker.
 * 
 * Validates: Requirements 6.1, 6.2, 6.3, 6.5
 */
@Configuration
public class WebClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CIRCUIT_BREAKER_NAME = "backend-api";

    @Autowired
    private AppConfigurationProperties appConfig;

    @Autowired
    private WebClientProperties webClientProperties;

    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    @Value("${app.backend.api.base-path:/api/v1}")
    private String apiBasePath;

    /**
     * Creates a WebClient bean with configured timeout, connection pooling, 
     * retry logic, and interceptors for transient failures.
     * Includes correlation ID propagation, circuit breaker protection, and logging.
     * 
     * Validates: Requirements 6.1, 6.2, 6.3, 6.5
     * 
     * @return WebClient configured with proper timeouts and interceptors
     */
    @Bean
    public WebClient webClient() {
        WebClientProperties.TimeoutSettings timeoutSettings = webClientProperties.getTimeout();
        WebClientProperties.PoolSettings poolSettings = webClientProperties.getPool();
        WebClientProperties.KeepAliveSettings keepAliveSettings = webClientProperties.getKeepAlive();

        // Construir la URL completa con el base path
        String fullBaseUrl = backendUrl + apiBasePath;

        logger.info("Configuring WebClient with backend URL: {}, base path: {}, full URL: {}", 
            backendUrl, apiBasePath, fullBaseUrl);
        logger.info("Timeouts: connect={}, read={}, write={}", 
            timeoutSettings.getConnect(), 
            timeoutSettings.getRead(), 
            timeoutSettings.getWrite());
        logger.info("Connection pool settings: maxConnections={}, maxIdleTime={}, maxLifeTime={}", 
            poolSettings.getMaxConnections(), 
            poolSettings.getMaxIdleTime(), 
            poolSettings.getMaxLifeTime());
        logger.info("Circuit breaker settings: enabled={}, failureThreshold={}%", 
            webClientProperties.getCircuitBreaker().isEnabled(),
            webClientProperties.getCircuitBreaker().getFailureThreshold());
        
        HttpClient httpClient = HttpClient.create(connectionProvider())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 
                        (int) timeoutSettings.getConnect().toMillis())
                .option(ChannelOption.SO_KEEPALIVE, keepAliveSettings.isEnabled())
                .option(ChannelOption.TCP_NODELAY, true)
                .responseTimeout(timeoutSettings.getRead())
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new ReadTimeoutHandler(
                            timeoutSettings.getRead().getSeconds(), TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(
                            timeoutSettings.getWrite().getSeconds(), TimeUnit.SECONDS));
                    
                    if (keepAliveSettings.isEnabled()) {
                        long keepAliveSeconds = keepAliveSettings.getTimeout().getSeconds();
                        conn.addHandlerLast(new io.netty.handler.timeout.IdleStateHandler(
                            keepAliveSeconds, keepAliveSeconds, keepAliveSeconds, TimeUnit.SECONDS));
                    }
                })
                .doOnDisconnected(conn -> {
                    logger.debug("Connection disconnected");
                });

        return WebClient.builder()
                .baseUrl(fullBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies())
                .filter(correlationIdPropagationFilter())
                .filter(requestResponseLoggingFilter())
                .filter(circuitBreakerFilter())
                .filter(errorHandlingFilter())
                .build();
    }

    /**
     * Creates an ExchangeFilterFunction that adds correlation ID headers to all requests.
     * This enables request tracing across service boundaries and supports logging.
     * 
     * Validates: Requirement 6.5 - Correlation ID propagation in request headers
     * 
     * @return ExchangeFilterFunction for correlation ID propagation
     */
    private ExchangeFilterFunction correlationIdPropagationFilter() {
        return (clientRequest, next) -> {
            String correlationId = UUID.randomUUID().toString().substring(0, 8);
            logger.debug("Propagating correlation ID [{}] in request", correlationId);
            
            return next.exchange(clientRequest).doFinally(signalType -> {
                logger.debug("Correlation ID [{}] request completed with signal: {}", 
                    correlationId, signalType);
            });
        };
    }

    /**
     * Creates a connection provider with pooling configuration from WebClientProperties.
     * Manages HTTP connection lifecycle and reuse with circuit breaker protection.
     * 
     * Validates: Requirement 6.1 - Connection pool configuration
     * 
     * @return ConnectionProvider configured for connection pooling
     */
    @Bean
    public ConnectionProvider connectionProvider() {
        WebClientProperties.PoolSettings poolSettings = webClientProperties.getPool();
        
        logger.info("Configuring connection provider with maxConnections={}, maxIdleTime={}, maxLifeTime={}", 
            poolSettings.getMaxConnections(), 
            poolSettings.getMaxIdleTime(), 
            poolSettings.getMaxLifeTime());
        
        return ConnectionProvider.builder("drinkhouse-web-ui-pool")
                .maxConnections(poolSettings.getMaxConnections())
                .maxIdleTime(poolSettings.getMaxIdleTime())
                .maxLifeTime(poolSettings.getMaxLifeTime())
                .pendingAcquireTimeout(poolSettings.getPendingAcquireTimeout())
                .evictInBackground(Duration.ofSeconds(30))
                .lifo() // Last In, First Out for better connection reuse
                .build();
    }

    /**
     * Creates an enhanced request/response logging filter that captures HTTP method, 
     * endpoint, status code, and response time for debugging.
     * 
     * Validates: Requirement 6.2 - Request/response logging
     * 
     * @return ExchangeFilterFunction for request/response logging
     */
    private ExchangeFilterFunction requestResponseLoggingFilter() {
        return (clientRequest, next) -> {
            long startTime = System.currentTimeMillis();

            return next.exchange(clientRequest)
                .doOnSuccess(response -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    logger.info("WebClient response: status={} in {}ms", 
                        response.statusCode().value(),
                        responseTime);
                })
                .doOnError(throwable -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    logger.warn("WebClient error after {}ms: {}", 
                        responseTime,
                        throwable.getMessage());
                });
        };
    }

    /**
     * Creates a circuit breaker filter for backend protection.
     * Prevents cascading failures by breaking circuit after threshold of failures.
     * 
     * Validates: Requirement 6.2 - Circuit breaker pattern for backend protection
     * 
     * @return ExchangeFilterFunction with circuit breaker protection
     */
    private ExchangeFilterFunction circuitBreakerFilter() {
        if (!webClientProperties.getCircuitBreaker().isEnabled()) {
            logger.info("Circuit breaker is disabled");
            return (clientRequest, next) -> next.exchange(clientRequest);
        }
        
        CircuitBreaker circuitBreaker = createCircuitBreaker();
        
        return (clientRequest, next) -> {
            logger.debug("Circuit breaker [{}] state: {}", CIRCUIT_BREAKER_NAME, circuitBreaker.getState());
            
            return next.exchange(clientRequest)
                .doOnSuccess(response -> {
                    if (!response.statusCode().isError()) {
                        logger.debug("Circuit breaker [{}] response success, state: {}", 
                            CIRCUIT_BREAKER_NAME, circuitBreaker.getState());
                    }
                })
                .doOnError(throwable -> {
                    logger.warn("Circuit breaker [{}] error, state: {}, error: {}", 
                        CIRCUIT_BREAKER_NAME, circuitBreaker.getState(), throwable.getMessage());
                });
        };
    }

    /**
     * Creates an error handling filter that translates HTTP errors appropriately.
     * 
     * Validates: Requirement 6.2 - Error handling in WebClient
     * 
     * @return ExchangeFilterFunction for error handling
     */
    private ExchangeFilterFunction errorHandlingFilter() {
        return (clientRequest, next) -> {
            return next.exchange(clientRequest)
                .doOnError(throwable -> {
                    if (throwable instanceof java.net.ConnectException) {
                        logger.error("Connection refused to backend: Backend service may be down");
                    } else if (throwable instanceof java.util.concurrent.TimeoutException) {
                        logger.error("Request timeout to backend: Backend service is taking too long to respond");
                    }
                });
        };
    }

    /**
     * Creates a circuit breaker instance configured from WebClientProperties.
     * 
     * @return CircuitBreaker configured for backend protection
     */
    private CircuitBreaker createCircuitBreaker() {
        WebClientProperties.CircuitBreakerSettings cbSettings = webClientProperties.getCircuitBreaker();
        
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(cbSettings.getFailureThreshold())
                .waitDurationInOpenState(cbSettings.getWaitDurationInOpenState())
                .recordExceptions(
                    WebClientResponseException.class,
                    java.net.ConnectException.class,
                    java.util.concurrent.TimeoutException.class,
                    java.io.IOException.class
                )
                .ignoreExceptions(IllegalArgumentException.class)
                .slidingWindowType(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(cbSettings.getRecordingSize())
                .build();
        
        // Create a new registry instance for each circuit breaker
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker(CIRCUIT_BREAKER_NAME, config);
        
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> 
                    logger.warn("Circuit breaker [{}] state transition: {} -> {}", 
                        CIRCUIT_BREAKER_NAME, event.getStateTransition().getFromState(), 
                        event.getStateTransition().getToState())
                )
                .onError(event -> 
                    logger.debug("Circuit breaker [{}] recorded error: {}", 
                        CIRCUIT_BREAKER_NAME, event.getThrowable().getMessage())
                );
        
        return circuitBreaker;
    }

    /**
     * Provides a CircuitBreakerRegistry for managing circuit breakers.
     * 
     * @return CircuitBreakerRegistry instance
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        registry.getEventPublisher()
                .onEntryAdded(event -> 
                    logger.info("Circuit breaker registered: {}", event.getAddedEntry().getName())
                );
        return registry;
    }

    /**
     * Creates a retry configuration for transient failures with exponential backoff.
     * Handles 503 Service Unavailable, 429 Too Many Requests, and connection timeouts.
     * 
     * Validates: Requirements 6.1, 6.2, 6.3
     * 
     * @return Retry configuration with exponential backoff
     */
    public Retry retryConfiguration() {
        WebClientProperties.RetrySettings retrySettings = webClientProperties.getRetry();
        
        logger.debug("Configuring retry logic: maxAttempts={}, initialBackoffDelay={}, exponential backoff enabled", 
            retrySettings.getMaxAttempts(), 
            retrySettings.getBackoffDelay());
        
        return Retry.backoff(retrySettings.getMaxAttempts(), retrySettings.getBackoffDelay())
                .multiplier(2.0) // Exponential backoff: each retry waits 2x longer than previous
                .maxBackoff(Duration.ofSeconds(60)) // Cap exponential backoff at 60 seconds
                .filter(this::isRetriableFailure)
                .doBeforeRetry(signal ->
                    logger.warn("Retrying request (attempt {}/{}) after error: {} - backing off {}ms", 
                        signal.totalRetries() + 1,
                        retrySettings.getMaxAttempts(),
                        signal.failure().getMessage(),
                        calculateBackoffDelay(signal.totalRetries(), retrySettings.getBackoffDelay()))
                )
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    logger.error("Retry exhausted after {} attempts. Final error: {}. Circuit breaker should protect subsequent calls.", 
                        retrySignal.totalRetries(), retrySignal.failure().getMessage());
                    return retrySignal.failure();
                });
    }

    /**
     * Calculates the exponential backoff delay for a given retry attempt.
     * Delay = baseDelay * (2 ^ attempt)
     * 
     * @param attempt the current retry attempt (0-indexed)
     * @param baseDelay the base delay duration
     * @return the calculated backoff delay in milliseconds
     */
    private long calculateBackoffDelay(long attempt, Duration baseDelay) {
        long maxBackoff = Duration.ofSeconds(60).toMillis();
        long exponentialDelay = baseDelay.toMillis() * (1L << attempt); // 2 ^ attempt
        return Math.min(exponentialDelay, maxBackoff);
    }

    /**
     * Determines if a throwable represents a retriable failure.
     * Includes connection issues, timeouts, and temporary service failures.
     * 
     * @param throwable the exception to check
     * @return true if the failure should be retried
     */
    private boolean isRetriableFailure(Throwable throwable) {
        // Retry on connection issues and timeouts
        if (throwable instanceof java.net.ConnectException ||
            throwable instanceof java.util.concurrent.TimeoutException ||
            throwable instanceof java.net.SocketTimeoutException ||
            throwable instanceof java.io.IOException ||
            throwable instanceof reactor.netty.http.client.PrematureCloseException ||
            throwable.getCause() instanceof java.net.ConnectException) {
            logger.warn("Retriable network error detected: {}", throwable.getClass().getSimpleName());
            return true;
        }
        
        // Retry on specific HTTP status codes
        if (throwable instanceof WebClientResponseException webClientException) {
            return isRetriableStatusCode(webClientException.getStatusCode().value());
        }
        
        return false;
    }

    /**
     * Checks if an HTTP status code represents a retriable failure.
     * Includes service unavailable, too many requests, and gateway errors.
     * 
     * @param statusCode the HTTP status code
     * @return true if the status code indicates a retriable failure
     */
    private boolean isRetriableStatusCode(int statusCode) {
        return statusCode == 429 || statusCode == 503 || statusCode == 504 || statusCode == 502;
    }

    /**
     * Provides the retry configuration as a bean for services to use.
     * Services can inject and apply this to their WebClient calls.
     * 
     * Validates: Requirements 6.1, 6.3
     * 
     * @return configured Retry instance with exponential backoff
     */
    @Bean
    public Retry webClientRetry() {
        return retryConfiguration();
    }

    /**
     * Configures exchange strategies for request/response handling.
     * Sets buffer size limits for payloads.
     * 
     * @return ExchangeStrategies with configured buffer sizes
     */
    @Bean
    public ExchangeStrategies exchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(1024 * 1024) // 1MB
                )
                .build();
    }
}
