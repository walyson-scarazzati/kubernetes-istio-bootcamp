package com.course.devops.yellow.adapter.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "${devops.service.white.root-url}", name = "whiteApiClient")
public interface WhiteApiClient {

  @GetMapping(path = "/api/hello")
  String hello();

  @GetMapping(path = "/api/echo")
  String echo();
  
  @GetMapping(value = "/api/status/{response-status-code}", produces = MediaType.TEXT_PLAIN_VALUE)
  ResponseEntity<String> status(
      @PathVariable(name = "response-status-code", required = true) String responseStatusCode);

}
