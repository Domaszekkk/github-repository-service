package com.example.githubrepositoryservice;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;


public class GithubRetryErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 503) {
            return new RetryableException(
                    response.status(),
                    "Service Unavailable - retrying",
                    response.request().httpMethod(),
                    (Long) null,
                    response.request()
            );
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}