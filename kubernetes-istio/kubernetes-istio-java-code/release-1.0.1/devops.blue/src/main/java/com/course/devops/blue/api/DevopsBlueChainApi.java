package com.course.devops.blue.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.devops.blue.adapter.api.WhiteApiClient;
import com.course.devops.blue.adapter.api.YellowApiClient;
import com.course.devops.blue.constant.DevopsApiConstants;
import com.course.devops.blue.filter.ApiResponseFilter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chain")
@Tag(name = "DevOps-Blue chain call API", description = "DevOps-Blue chain call API")
@Validated
@Slf4j
public class DevopsBlueChainApi {

  private static final String CONTENT_YELLOW_STRING = "  - Yellow : %s";

  private static final String CONTENT_BLUE_STRING = "  - Blue : %s";

  private static final Map<String, List<Integer>> MAP_RESPONSE_STATUS_CODE = new HashMap<>();

  static {
    MAP_RESPONSE_STATUS_CODE.put(DevopsApiConstants.STATUS_2XX, List.of(200, 201, 202));
    MAP_RESPONSE_STATUS_CODE.put(DevopsApiConstants.STATUS_3XX, List.of(301, 302, 304, 307, 308));
    MAP_RESPONSE_STATUS_CODE.put(DevopsApiConstants.STATUS_4XX,
        List.of(400, 401, 403, 404, 405, 406, 408, 413, 415, 429, 431));
    MAP_RESPONSE_STATUS_CODE.put(DevopsApiConstants.STATUS_5XX, List.of(500, 501, 502, 503, 504));
    MAP_RESPONSE_STATUS_CODE.put(DevopsApiConstants.STATUS_RANDOM, List.of(200, 201, 202, 301, 302, 304, 307, 308, 400,
        401, 403, 404, 405, 406, 408, 413, 415, 429, 431, 500, 501, 502, 503, 504));
  }

  @Autowired
  private YellowApiClient yellowApiClient;

  @Autowired
  private WhiteApiClient whiteApiClient;

  @RequestMapping(value = "/echo/one",produces = MediaType.TEXT_PLAIN_VALUE) 
  public ResponseEntity<String> chainEchoOne(HttpServletRequest request, @RequestBody(required = false) byte[] rawBody) {
    log.info("Calling chain/echo/one");
    final var sb = new StringBuilder();
    
    final Map<String, Object> headersMap = Collections.list(request.getHeaderNames())    
        .stream()
        .collect(Collectors.toMap(
            Function.identity(), 
            h -> Collections.list(request.getHeaders(h))
        ));

    sb.append("Blue call yellow.echo. Final result is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_BLUE_STRING, echo(request, rawBody)));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, yellowApiClient.echo(headersMap)));

    return ResponseEntity.ok().body(sb.toString());
  }

  private String echo(HttpServletRequest request,byte[] rawBody) {
    log.info("Calling echo");
    final var sb = new StringBuilder();

    if (request.isSecure()) {
      sb.append("Protocol : " + request.getProtocol() + " (via secure TLS / HTTPS)\n\n");
    } else {
      sb.append("Protocol : " + request.getProtocol() + " (via plain HTTP)\n\n");
    }
    sb.append("Path : " + request.getServletPath() + "\n\n");
    sb.append("Method : " + request.getMethod() + "\n\n");
    sb.append("Headers :\n\n");
    request.getHeaderNames().asIterator().forEachRemaining(hn -> {
      request.getHeaders(hn).asIterator().forEachRemaining(hv -> {
        sb.append(String.format("  %s : %s", hn, hv));
        sb.append("\n");
      });
    });
    sb.append("\n");
    sb.append("Cookies : " + request.getCookies() + "\n\n");
    sb.append("Parameters :\n");
    request.getParameterMap().forEach((k, v) -> {
      sb.append(String.format("  %s : %s", k, Arrays.asList(v)));
      sb.append("\n");
    });
    sb.append("\n");
    sb.append("Body : " + (rawBody != null ? new String(rawBody) : null));

    return sb.toString();
  }

  @RequestMapping(value = "/echo/two",produces = MediaType.TEXT_PLAIN_VALUE) 
  public ResponseEntity<String> chainEchoTwo(HttpServletRequest request, @RequestBody(required = false) byte[] rawBody) {
    log.info("Calling chain/echo/two");
    final var sb = new StringBuilder();
    final Map<String, Object> headersMap = Collections.list(request.getHeaderNames())    
        .stream()
        .collect(Collectors.toMap(
            Function.identity(), 
            h -> Collections.list(request.getHeaders(h))
        ));

    sb.append("Blue call yellow.echo and yellow call white.echo. Final result is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_BLUE_STRING, echo(request, rawBody)));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, yellowApiClient.chainEcho(headersMap)));

    return ResponseEntity.ok().body(sb.toString());
  }

  @GetMapping(value = "/{response-status-code}/one", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Chain call to one other service, API from other service will produce 2xx, but this root API will produce response status code as requested on path variable")
  public ResponseEntity<String> chainOne(HttpServletRequest request, 
      @Pattern(regexp = "2xx|3xx|4xx|5xx|random") @PathVariable(value = "response-status-code", required = true) @Parameter(description = "Requested status code, or string <code>random</code>. Other values will generate response code 500.", example = "4xx", schema = @Schema(type = "string", allowableValues = {
          "random", "2xx", "3xx", "4xx", "5xx" })) String responseStatusCode) {
    log.info("Calling chain/one");
    final var sb = new StringBuilder();
    final var listStatusCode = MAP_RESPONSE_STATUS_CODE.get(responseStatusCode);
    final var statusCode = listStatusCode.get(RandomUtils.nextInt(0, listStatusCode.size()));
    final Map<String, Object> headersMap = Collections.list(request.getHeaderNames())    
        .stream()
        .collect(Collectors.toMap(
            Function.identity(), 
            h -> Collections.list(request.getHeaders(h))
        ));

    sb.append("Blue call yellow. Final response is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_BLUE_STRING, hello()));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, yellowApiClient.status(headersMap, DevopsApiConstants.STATUS_2XX)));

    return ResponseEntity.status(statusCode).body(sb.toString());
  }

  @GetMapping(value = "/{response-status-code}/two", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Chain call to two other services, API from other service will produce 2xx, but this root API will produce response status code as requested on path variable")
  public ResponseEntity<String> chainTwo(HttpServletRequest request, 
      @Pattern(regexp = "2xx|3xx|4xx|5xx|random") @PathVariable(value = "response-status-code", required = true) @Parameter(description = "Requested status code, or string <code>random</code>. Other values will generate response code 500.", example = "4xx", schema = @Schema(type = "string", allowableValues = {
          "random", "2xx", "3xx", "4xx", "5xx" })) String responseStatusCode) {
    log.info("Calling chain/two");
    final var sb = new StringBuilder();
    final var listStatusCode = MAP_RESPONSE_STATUS_CODE.get(responseStatusCode);
    final var statusCode = listStatusCode.get(RandomUtils.nextInt(0, listStatusCode.size()));
    final Map<String, Object> headersMap = Collections.list(request.getHeaderNames())    
        .stream()
        .collect(Collectors.toMap(
            Function.identity(), 
            h -> Collections.list(request.getHeaders(h))
        ));

    sb.append("Blue call yellow, and blue call white. Final response is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_BLUE_STRING, hello()));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, yellowApiClient.status(headersMap, DevopsApiConstants.STATUS_2XX)));
    sb.append(StringUtils.LF);
    sb.append(String.format("  - White : %s", whiteApiClient.status(DevopsApiConstants.STATUS_2XX)));

    return ResponseEntity.status(statusCode).body(sb.toString());
  }

  @GetMapping(value = "/{response-status-code}/three", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Chain call to one other service, which in turn call another service, API from other service will produce 2xx, but this root API will produce response status code as requested on path variable")
  public ResponseEntity<String> chainThree(HttpServletRequest request,
      @Pattern(regexp = "2xx|3xx|4xx|5xx|random") @PathVariable(value = "response-status-code", required = true) @Parameter(description = "Requested status code, or string <code>random</code>. Other values will generate response code 500.", example = "4xx", schema = @Schema(type = "string", allowableValues = {
          "random", "2xx", "3xx", "4xx", "5xx" })) String responseStatusCode) {
    log.info("Calling chain/three");
    final var sb = new StringBuilder();
    final var listStatusCode = MAP_RESPONSE_STATUS_CODE.get(responseStatusCode);
    final var statusCode = listStatusCode.get(RandomUtils.nextInt(0, listStatusCode.size()));
    final Map<String, Object> headersMap = Collections.list(request.getHeaderNames())    
        .stream()
        .collect(Collectors.toMap(
            Function.identity(), 
            h -> Collections.list(request.getHeaders(h))
        ));

    sb.append("Blue call yellow, and yellow call white. Final response is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_BLUE_STRING, hello()));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, yellowApiClient.chainOne( headersMap, DevopsApiConstants.STATUS_2XX)));

    return ResponseEntity.status(statusCode).body(sb.toString());
  }

  @GetMapping(value = "/{response-status-code}/four", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Chain call to one other service, API from other service will produce response status code as requested on path variable")
  public ResponseEntity<String> chainFour( HttpServletRequest request,
      @Pattern(regexp = "2xx|3xx|4xx|5xx|random") @PathVariable(value = "response-status-code", required = true) @Parameter(description = "Requested status code, or string <code>random</code>. Other values will generate response code 500.", example = "4xx", schema = @Schema(type = "string", allowableValues = {
          "random", "2xx", "3xx", "4xx", "5xx" })) String responseStatusCode) {
    log.info("Calling chain/four");
    final var sb = new StringBuilder();
    final var listStatusCode = MAP_RESPONSE_STATUS_CODE.get(DevopsApiConstants.STATUS_2XX);
    final var statusCode = listStatusCode.get(RandomUtils.nextInt(0, listStatusCode.size()));
    final Map<String, Object> headersMap = Collections.list(request.getHeaderNames())    
        .stream()
        .collect(Collectors.toMap(
            Function.identity(), 
            h -> Collections.list(request.getHeaders(h))
        ));

    sb.append("Blue call yellow. Final response is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_BLUE_STRING, hello()));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, yellowApiClient.status( headersMap, responseStatusCode)));

    return ResponseEntity.status(statusCode).body(sb.toString());
  }

  @GetMapping(value = "/{response-status-code}/five", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Chain call to one other service, which in turn call another service, API from other service or will produce response status code as requested on path variable")
  public ResponseEntity<String> chainFive(HttpServletRequest request,
      @Pattern(regexp = "2xx|3xx|4xx|5xx|random") @PathVariable(value = "response-status-code", required = true) @Parameter(description = "Requested status code, or string <code>random</code>. Other values will generate response code 500.", example = "4xx", schema = @Schema(type = "string", allowableValues = {
          "random", "2xx", "3xx", "4xx", "5xx" })) String responseStatusCode) {
    log.info("Calling chain/five");
    final var sb = new StringBuilder();
    final var listStatusCode = MAP_RESPONSE_STATUS_CODE.get(DevopsApiConstants.STATUS_2XX);
    final var statusCode = listStatusCode.get(RandomUtils.nextInt(0, listStatusCode.size()));
    final Map<String, Object> headersMap = Collections.list(request.getHeaderNames())    
        .stream()
        .collect(Collectors.toMap(
            Function.identity(), 
            h -> Collections.list(request.getHeaders(h))
        ));

    sb.append("Blue call yellow, and yellow call white. Final response is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_BLUE_STRING, hello()));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, yellowApiClient.chainOne(headersMap, responseStatusCode)));

    return ResponseEntity.status(statusCode).body(sb.toString());
  }

  private String hello() {
    return String.format("Version [%s] Hello from app [%s] on k8s pod [%s]", ApiResponseFilter.APP_VERSION,
        ApiResponseFilter.getAppIdentifier(), ApiResponseFilter.getPodName());
  }

}