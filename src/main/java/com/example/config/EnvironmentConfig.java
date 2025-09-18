package com.example.config;

import java.io.InputStream;
import java.util.Properties;

public class EnvironmentConfig {

    private final Properties properties;

    public EnvironmentConfig(String env) {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config-" + env + ".properties")) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
