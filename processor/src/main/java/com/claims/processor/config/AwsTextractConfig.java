package com.claims.processor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
@Configuration
public class AwsTextractConfig {
     @Bean
    public TextractClient textractClient() {
        return TextractClient.builder()
                .region(Region.AP_SOUTH_1) 
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
