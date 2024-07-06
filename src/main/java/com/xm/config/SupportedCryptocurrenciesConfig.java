package com.xm.config;

import com.xm.config.factory.YamlPropertySourceFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Set;

@Configuration
@PropertySource(value = "classpath:data/supported-cryptocurrencies.yaml",
        factory = YamlPropertySourceFactory.class)
@ConfigurationProperties()
@Getter
@Setter
@NoArgsConstructor
public class SupportedCryptocurrenciesConfig {

    private Set<String> cryptocurrencies;
}
