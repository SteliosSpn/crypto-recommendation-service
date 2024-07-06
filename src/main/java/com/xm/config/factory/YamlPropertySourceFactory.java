package com.xm.config.factory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Objects;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    @SuppressWarnings({"NullableProblems"})
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());

        Properties properties = factory.getObject();

        String fileName = encodedResource.getResource().getFilename();

        if (Objects.isNull(fileName)) {
            throw new NullPointerException();
        }

        return new PropertiesPropertySource(Objects.requireNonNull(fileName), Objects.requireNonNull(properties));
    }
}
