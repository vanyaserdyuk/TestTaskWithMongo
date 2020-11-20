package ru.testtask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("test")
@PropertySource("classpath:/default.properties")
@PropertySource(value = "classpath:/local.properties", ignoreResourceNotFound = true)
public class TestConfig {

}
