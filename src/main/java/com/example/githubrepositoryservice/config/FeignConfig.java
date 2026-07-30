package com.example.githubrepositoryservice.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new GithubRetryErrorDecoder();
    }
}