package com.course.devops.blue.adapter.api;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "${devops.service.yellow.root-url}", name = "yellowApiClient")
public interface YellowApiClient {
  @GetMapping(path = "/api/hello")
  String hello();

  @GetMapping(path = "/api/echo")
  String echo(@RequestHeader Map<String, Object> headers);

  @GetMapping(path = "/api/chain/echo/one")
  String chainEcho(@RequestHeader Map<String, Object> headers);

  @GetMapping(path = "/api/chain/{response-status-code}/one")
  ResponseEntity<String> chainOne(
    @RequestHeader Map<String, Object> headers,
    @PathVariable("response-status-code") String responseStatusCode
  );

  @GetMapping(path = "/api/chain/{response-status-code}/four")
  ResponseEntity<String> chainFour(
    @RequestHeader Map<String, Object> headers,
    @PathVariable("response-status-code") String responseStatusCode
  );

  @GetMapping(
    value = "/api/status/{response-status-code}",
    produces = MediaType.TEXT_PLAIN_VALUE
  )
  ResponseEntity<String> status(
    @RequestHeader Map<String, Object> headers,
    @PathVariable(
      name = "response-status-code",
      required = true
    ) String responseStatusCode
  );
}
