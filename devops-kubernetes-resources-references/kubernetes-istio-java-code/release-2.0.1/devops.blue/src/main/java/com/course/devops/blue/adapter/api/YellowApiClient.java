package com.course.devops.blue.adapter.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class YellowApiClient {

    private final RestClient restClient;
    private final String yellowServiceUrl;

    public YellowApiClient(RestClient restClient,
                           @Value("${devops.service.yellow.root-url}") String yellowServiceUrl) {
        this.restClient = restClient;
        this.yellowServiceUrl = yellowServiceUrl;
    }

    public String hello() {
        return restClient.get()
                .uri(yellowServiceUrl + "/api/hello")
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .body(String.class);
    }

    public String echo(Map<String, Object> headers) {
        return restClient.get()
                .uri(yellowServiceUrl + "/api/echo")
                .accept(MediaType.TEXT_PLAIN)
                .headers(httpHeaders -> headers.forEach((key, value) -> {
                    if (value instanceof List) {
                        ((List<?>) value).forEach(v -> 
                            httpHeaders.add(key, String.valueOf(v)));
                    } else {
                        httpHeaders.add(key, String.valueOf(value));
                    }
                }))
                .retrieve()
                .body(String.class);
    }

    public String chainEcho(Map<String, Object> headers) {
        return restClient.get()
                .uri(yellowServiceUrl + "/api/chain/echo/one")
                .accept(MediaType.TEXT_PLAIN)
                .headers(httpHeaders -> headers.forEach((key, value) -> {
                    if (value instanceof List) {
                        ((List<?>) value).forEach(v -> 
                            httpHeaders.add(key, String.valueOf(v)));
                    } else {
                        httpHeaders.add(key, String.valueOf(value));
                    }
                }))
                .retrieve()
                .body(String.class);
    }

    public ResponseEntity<String> chainOne(Map<String, Object> headers, String responseStatusCode) {
        return restClient.get()
                .uri(yellowServiceUrl + "/api/chain/{code}/one", responseStatusCode)
                .accept(MediaType.TEXT_PLAIN)
                .headers(httpHeaders -> headers.forEach((key, value) -> {
                    if (value instanceof List) {
                        ((List<?>) value).forEach(v -> 
                            httpHeaders.add(key, String.valueOf(v)));
                    } else {
                        httpHeaders.add(key, String.valueOf(value));
                    }
                }))
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<String> chainFour(Map<String, Object> headers, String responseStatusCode) {
        return restClient.get()
                .uri(yellowServiceUrl + "/api/chain/{code}/four", responseStatusCode)
                .accept(MediaType.TEXT_PLAIN)
                .headers(httpHeaders -> headers.forEach((key, value) -> {
                    if (value instanceof List) {
                        ((List<?>) value).forEach(v -> 
                            httpHeaders.add(key, String.valueOf(v)));
                    } else {
                        httpHeaders.add(key, String.valueOf(value));
                    }
                }))
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<String> status(Map<String, Object> headers, String responseStatusCode) {
        return restClient.get()
                .uri(yellowServiceUrl + "/api/status/{code}", responseStatusCode)
                .accept(MediaType.TEXT_PLAIN)
                .headers(httpHeaders -> headers.forEach((key, value) -> {
                    if (value instanceof List) {
                        ((List<?>) value).forEach(v -> 
                            httpHeaders.add(key, String.valueOf(v)));
                    } else {
                        httpHeaders.add(key, String.valueOf(value));
                    }
                }))
                .retrieve()
                .toEntity(String.class);
    }
}