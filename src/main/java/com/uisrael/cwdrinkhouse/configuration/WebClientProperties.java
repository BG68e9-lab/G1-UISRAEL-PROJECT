package com.uisrael.cwdrinkhouse.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Configuration properties for WebClient timeout, retry, and connection pool settings.
 * This class binds to the "app.webclient" prefix in application properties.
 * 
 * Validates: Requirements 6.1, 6.3, 6.4
 */
@Component
@ConfigurationProperties(prefix = "app.webclient")
public class WebClientProperties {

    /**
     * Timeout settings for WebClient connections
     */
    public static class TimeoutSettings {
        private Duration connect = Duration.ofSeconds(30);
        private Duration read = Duration.ofMinutes(2);
        private Duration write = Duration.ofSeconds(30);

        public Duration getConnect() {
            return connect;
        }

        public void setConnect(Duration connect) {
            this.connect = connect;
        }

        public Duration getRead() {
            return read;
        }

        public void setRead(Duration read) {
            this.read = read;
        }

        public Duration getWrite() {
            return write;
        }

        public void setWrite(Duration write) {
            this.write = write;
        }
    }

    /**
     * Retry configuration with exponential backoff settings
     */
    public static class RetrySettings {
        private int maxAttempts = 3;
        private Duration backoffDelay = Duration.ofSeconds(2);

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public Duration getBackoffDelay() {
            return backoffDelay;
        }

        public void setBackoffDelay(Duration backoffDelay) {
            this.backoffDelay = backoffDelay;
        }
    }

    /**
     * Connection pooling configuration for optimal performance
     */
    public static class PoolSettings {
        private int maxConnections = 50;
        private Duration maxIdleTime = Duration.ofSeconds(30);
        private Duration maxLifeTime = Duration.ofSeconds(300);
        private Duration pendingAcquireTimeout = Duration.ofSeconds(60);

        public int getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public Duration getMaxIdleTime() {
            return maxIdleTime;
        }

        public void setMaxIdleTime(Duration maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        public Duration getMaxLifeTime() {
            return maxLifeTime;
        }

        public void setMaxLifeTime(Duration maxLifeTime) {
            this.maxLifeTime = maxLifeTime;
        }

        public Duration getPendingAcquireTimeout() {
            return pendingAcquireTimeout;
        }

        public void setPendingAcquireTimeout(Duration pendingAcquireTimeout) {
            this.pendingAcquireTimeout = pendingAcquireTimeout;
        }
    }

    /**
     * Keep-alive connection settings
     */
    public static class KeepAliveSettings {
        private boolean enabled = true;
        private Duration timeout = Duration.ofSeconds(60);

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Duration getTimeout() {
            return timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }
    }

    /**
     * Circuit breaker configuration for backend protection
     */
    public static class CircuitBreakerSettings {
        private boolean enabled = true;
        private int failureThreshold = 50; // Percentage
        private int successThreshold = 2; // Number of successful calls to close circuit
        private Duration waitDurationInOpenState = Duration.ofSeconds(60);
        private Duration recordingDuration = Duration.ofSeconds(15); // Sliding window in seconds
        private int recordingSize = 100; // Number of calls to record

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getFailureThreshold() {
            return failureThreshold;
        }

        public void setFailureThreshold(int failureThreshold) {
            this.failureThreshold = failureThreshold;
        }

        public int getSuccessThreshold() {
            return successThreshold;
        }

        public void setSuccessThreshold(int successThreshold) {
            this.successThreshold = successThreshold;
        }

        public Duration getWaitDurationInOpenState() {
            return waitDurationInOpenState;
        }

        public void setWaitDurationInOpenState(Duration waitDurationInOpenState) {
            this.waitDurationInOpenState = waitDurationInOpenState;
        }

        public Duration getRecordingDuration() {
            return recordingDuration;
        }

        public void setRecordingDuration(Duration recordingDuration) {
            this.recordingDuration = recordingDuration;
        }

        public int getRecordingSize() {
            return recordingSize;
        }

        public void setRecordingSize(int recordingSize) {
            this.recordingSize = recordingSize;
        }
    }

    private TimeoutSettings timeout = new TimeoutSettings();
    private RetrySettings retry = new RetrySettings();
    private PoolSettings pool = new PoolSettings();
    private KeepAliveSettings keepAlive = new KeepAliveSettings();
    private CircuitBreakerSettings circuitBreaker = new CircuitBreakerSettings();

    public TimeoutSettings getTimeout() {
        return timeout;
    }

    public void setTimeout(TimeoutSettings timeout) {
        this.timeout = timeout;
    }

    public RetrySettings getRetry() {
        return retry;
    }

    public void setRetry(RetrySettings retry) {
        this.retry = retry;
    }

    public PoolSettings getPool() {
        return pool;
    }

    public void setPool(PoolSettings pool) {
        this.pool = pool;
    }

    public KeepAliveSettings getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(KeepAliveSettings keepAlive) {
        this.keepAlive = keepAlive;
    }

    public CircuitBreakerSettings getCircuitBreaker() {
        return circuitBreaker;
    }

    public void setCircuitBreaker(CircuitBreakerSettings circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }
}
