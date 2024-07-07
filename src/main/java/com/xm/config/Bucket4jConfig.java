package com.xm.config;

import com.giffing.bucket4j.spring.boot.starter.config.cache.AsyncCacheResolver;
import com.giffing.bucket4j.spring.boot.starter.config.cache.hazelcast.HazelcastCacheResolver;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bucket4jConfig {

    @Bean
    public AsyncCacheResolver bucket4jHazelcastCacheResolver(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheResolver(hazelcastInstance, true);
    }

}
