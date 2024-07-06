package com.xm.config;

import com.xm.service.CryptoPriceParsingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Configuration
@IntegrationComponentScan
@Slf4j
@RequiredArgsConstructor
public class IntegrationConfig {

    @Value("${crypto.prices.directory}")
    private String cryptoPricesDirectory;
    private final ResourceLoader resourceLoader;
    private final CryptoPriceParsingService cryptoPriceParsingService;

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public StandardIntegrationFlow fileReadingFlow() throws IOException {
        log.info("Initializing file reading flow for directory: {}", cryptoPricesDirectory);
        Resource resource = resourceLoader.getResource(cryptoPricesDirectory);
        Path directoryPath = resource.getFile().toPath();

        return IntegrationFlow
                .from(Files.inboundAdapter(new File(directoryPath.toString()))
                                .patternFilter("*.csv")
                                .preventDuplicates(true),
                        endpointConfig -> endpointConfig.poller(p -> p.fixedDelay(1000).maxMessagesPerPoll(1)))
                .transform(new FileToStringTransformer())
                .channel(fileInputChannel())
                .handle(fileMessageHandler())
                .get();
    }

    @Bean
    @ServiceActivator(inputChannel = "fileInputChannel")
    public MessageHandler fileMessageHandler() {
        return message -> {
            String payload = (String) message.getPayload();
            cryptoPriceParsingService.processCsvContent(payload);
            log.info("Cryptocurrency file " + message.getHeaders()+ " processed successfully.");
        };
    }

}
