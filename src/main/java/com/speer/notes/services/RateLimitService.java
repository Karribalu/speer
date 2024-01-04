package com.speer.notes.services;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private long rateLimit;
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public RateLimitService(@Value("${rate_limit_per_minute}")
                            long rateLimit) {
        this.rateLimit = rateLimit;
    }

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(rateLimit, Refill.intervally(rateLimit, Duration.ofMinutes(1))))
                .build();
    }
}
