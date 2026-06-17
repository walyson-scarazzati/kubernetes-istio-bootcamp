package com.course.devops.yellow.adapter.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WhiteApiClient {

    private final RestClient restClient;
    private final String whiteServiceUrl;

    public WhiteApiClient(RestClient restClient,
            @Value("${devops.service.white.root-url}") String whiteServiceUrl) {
        this.restClient = restClient;
        this.whiteServiceUrl = whiteServiceUrl;
    }

    public String hello(Map<String, Object> headers) {
        return restClient.get()
                .uri(whiteServiceUrl + "/api/hello")
                .headers(httpHeaders -> headers.forEach((key, value) -> {
                    if (value instanceof List) {
                        ((List<?>) value).forEach(v -> httpHeaders.add(key, String.valueOf(v)));
                    } else {
                        httpHeaders.add(key, String.valueOf(value));
                    }
                }))
                .retrieve()
                .body(String.class);
    }

    public String echo(Map<String, Object> headers) {
        return restClient.get()
                .uri(whiteServiceUrl + "/api/echo")
                .headers(httpHeaders -> headers.forEach((key, value) -> {
                    if (value instanceof List) {
                        ((List<?>) value).forEach(v -> httpHeaders.add(key, String.valueOf(v)));
                    } else {
                        httpHeaders.add(key, String.valueOf(value));
                    }
                }))
                .retrieve()
                .body(String.class);
    }

    public ResponseEntity<String> status(Map<String, Object> headers, String responseStatusCode) {
        return restClient.get()
                .uri(whiteServiceUrl + "/api/status/{code}", responseStatusCode)
                .accept(MediaType.TEXT_PLAIN)
                .headers(httpHeaders -> headers.forEach((key, value) -> {
                    if (value instanceof List) {
                        ((List<?>) value).forEach(v -> httpHeaders.add(key, String.valueOf(v)));
                    } else {
                        httpHeaders.add(key, String.valueOf(value));
                    }
                }))
                .retrieve()
                .toEntity(String.class);
    }
}