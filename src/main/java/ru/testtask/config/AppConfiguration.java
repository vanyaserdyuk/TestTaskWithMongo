package ru.testtask.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfiguration {
    @Configuration
    @PropertySource("classpath:application-default.properties")
    static class Defaults
    { }

    @Configuration
    @PropertySource({"classpath:application-default.properties", "classpath:application-local.properties"})
    static class Overrides
    {

    }

    @Autowired
    private Environment environment;

    @Bean
    public Bean bean() {
        return this.environment.getRequiredProperty();
    }


}
