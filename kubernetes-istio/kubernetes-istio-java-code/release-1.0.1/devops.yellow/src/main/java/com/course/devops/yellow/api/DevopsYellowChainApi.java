package com.course.devops.yellow.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.devops.yellow.adapter.api.WhiteApiClient;
import com.course.devops.yellow.constant.DevopsApiConstants;
import com.course.devops.yellow.filter.ApiResponseFilter;

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
public class DevopsYellowChainApi {

  private static final String CONTENT_YELLOW_STRING = "  - Yellow : %s";

  private static final String CONTENT_WHITE_STRING = "  - White : %s";

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
  private WhiteApiClient whiteApiClient;

  @RequestMapping(value = "/echo/one",produces = MediaType.TEXT_PLAIN_VALUE) 
  public ResponseEntity<String> chainEchoOne(HttpServletRequest request) {
    log.info("Calling chain/echo/one");
    final var sb = new StringBuilder();

    sb.append("Yellow call white.echo. Echo in white is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, echo(request)));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_WHITE_STRING, whiteApiClient.echo()));

    return ResponseEntity.ok().body(sb.toString());
  }

  
  private String echo(HttpServletRequest request){
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

    return sb.toString();}


  @GetMapping(value = "/{response-status-code}/one", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Chain call to one other service, API from other service will produce response status code as requested on path variable")
  public ResponseEntity<String> chainOne(
      @Pattern(regexp = "2xx|3xx|4xx|5xx|random") @PathVariable(value = "response-status-code", required = true) @Parameter(description = "Requested status code, or string <code>random</code>. Other values will generate response code 500.", example = "4xx", schema = @Schema(type = "string", allowableValues = {
          "random", "2xx", "3xx", "4xx", "5xx" })) String responseStatusCode) {
    log.info("Calling chain/one");
    final var sb = new StringBuilder();
    final var listStatusCode = MAP_RESPONSE_STATUS_CODE.get(DevopsApiConstants.STATUS_2XX);
    final var statusCode = listStatusCode.get(RandomUtils.nextInt(0, listStatusCode.size()));

    sb.append("Yellow call white. Final response is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, hello()));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_WHITE_STRING, whiteApiClient.status(responseStatusCode)));

    return ResponseEntity.status(statusCode).body(sb.toString());
  }

  @GetMapping(value = "/{response-status-code}/four", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Chain call to one other service, API from other service will produce response status code as requested on path variable")
  public ResponseEntity<String> chainFour(
      @Pattern(regexp = "2xx|3xx|4xx|5xx|random") @PathVariable(value = "response-status-code", required = true) @Parameter(description = "Requested status code, or string <code>random</code>. Other values will generate response code 500.", example = "4xx", schema = @Schema(type = "string", allowableValues = {
          "random", "2xx", "3xx", "4xx", "5xx" })) String responseStatusCode) {
    log.info("Calling chain/four");
    final var sb = new StringBuilder();
    final var listStatusCode = MAP_RESPONSE_STATUS_CODE.get(DevopsApiConstants.STATUS_2XX);
    final var statusCode = listStatusCode.get(RandomUtils.nextInt(0, listStatusCode.size()));

    sb.append("Yellow call white. Final response is :");
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_YELLOW_STRING, hello()));
    sb.append(StringUtils.LF);
    sb.append(String.format(CONTENT_WHITE_STRING, whiteApiClient.status(responseStatusCode)));

    return ResponseEntity.status(statusCode).body(sb.toString());
  }

  private String hello() {
    return String.format("Version [%s] Hello from app [%s] on k8s pod [%s]", ApiResponseFilter.APP_VERSION,
        ApiResponseFilter.getAppIdentifier(), ApiResponseFilter.getPodName());
  }

}