package com.course.devops.blue.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient restClient(ObservationRegistry observationRegistry) {
        return RestClient.builder()
                .observationRegistry(observationRegistry)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            // Don't throw for 4xx
                        })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            // Don't throw for 5xx
                        })
                .build();
    }
}