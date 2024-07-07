package com.xm.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor {

    @Value("${ratelimit.maxTokens}")
    private long maxTokens;

    @Value("${ratelimit.refilledTokens}")
    private long refilledTokens;

    @Value("${ratelimit.refillPeriod}")
    private long refillPeriod;

    private final HazelcastProxyManager<String> proxyManager;
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public RateLimitingInterceptor(HazelcastProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
        executor.scheduleAtFixedRate(this::cleanupInactiveIPs, 1, 10, TimeUnit.MINUTES);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ipAddress, this::createNewBucket);

        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for IP: {} URL: {}", ipAddress, request.getRequestURI());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
            return false;
        }
        return true;
    }

    private Bucket createNewBucket(String ipAddress) {
        Supplier<BucketConfiguration> configurationSupplier = () -> BucketConfiguration.builder()
                .addLimit(Bandwidth
                        .builder()
                        .capacity(maxTokens)
                        .refillGreedy(refilledTokens, Duration.ofMinutes(refillPeriod)).build())
                .build();

        return proxyManager.builder().build(ipAddress, configurationSupplier);
    }

    private void cleanupInactiveIPs() {
        buckets.entrySet().removeIf(entry -> {
            Bucket bucket = entry.getValue();
            return bucket.getAvailableTokens() == maxTokens;
        });
    }
}
