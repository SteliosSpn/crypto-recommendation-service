package com.xm.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class HazelcastConfig {

    @Bean(name = "hazelcastConfigBean")
    public Config hazelcastConfig() {
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(false);
        config.addMapConfig(new MapConfig("hazelcast-buckets"));
        return config;
    }

    @Bean
    @Primary
    public HazelcastInstance hazelcastInstance(Config hazelcastConfigBean) {
        HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName("hazelcast-instance");
        if (instance == null) {
            instance = Hazelcast.newHazelcastInstance(hazelcastConfigBean);
        }
        return instance;
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

    @Bean
    public IMap<String, byte[]> rateLimitingMap(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("hazelcast-buckets");
    }

    @Bean
    public HazelcastProxyManager<String> hazelcastProxyManager(IMap<String, byte[]> rateLimitingMap) {
        return new HazelcastProxyManager<>(rateLimitingMap);
    }
}
