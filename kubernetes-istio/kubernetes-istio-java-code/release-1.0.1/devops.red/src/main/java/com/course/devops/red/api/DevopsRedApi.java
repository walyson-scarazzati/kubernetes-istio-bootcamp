package com.course.devops.red.api;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.course.devops.red.api.response.HelloTimeResponse;
import com.course.devops.red.constant.DevopsApiConstants;
import com.course.devops.red.filter.ApiResponseFilter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Tag(name = "DevOps-Red custom API", description = "DevOps-Red custom API")
@Slf4j
public class DevopsRedApi {

  private static final String CONTENT_RETURN_AFTER_DELAY = "Return after %s second delay";

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

  @GetMapping(value = "/delay/{delay-second}", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Send a process, delayed for x second")
  public String delay(
      @Valid @Min(0) @Max(30) @PathVariable(name = "delay-second", required = true) @Parameter(description = "Delay time (in second)", example = "10") long delaySecond)
      throws InterruptedException {
    TimeUnit.SECONDS.sleep(delaySecond);

    return String.format(CONTENT_RETURN_AFTER_DELAY, delaySecond);
  }

  @GetMapping(value = "/delay", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Send a process, delayed between min-max second (defined in request param). "
      + "If parameter is undefined, default to 0, and max default to 20.")
  public String delayBetween(
      @Min(0) @Max(30) @RequestParam(name = "min-delay-second", required = false, defaultValue = "0") @Parameter(description = "Min delay time (in second), default to 0", example = "2", schema = @Schema(defaultValue = "0", minimum = "0", maximum = "30", exclusiveMaximum = true)) long minDelaySecond,
      @Min(0) @Max(30) @RequestParam(name = "max-delay-second", required = false, defaultValue = "20") @Parameter(description = "Max delay time (in second), default to 30. If max < min, max will be set to min.", example = "16") long maxDelaySecond)
      throws InterruptedException {
    if (maxDelaySecond < minDelaySecond) {
      maxDelaySecond = minDelaySecond;
    }

    var delaySecond = ThreadLocalRandom.current().nextLong(minDelaySecond, maxDelaySecond);
    TimeUnit.SECONDS.sleep(delaySecond);

    return String.format(CONTENT_RETURN_AFTER_DELAY, delaySecond);
  }

  @RequestMapping(value = "/echo", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Echoing request")
  public String echo(HttpServletRequest request, @RequestBody(required = false) byte[] rawBody) {
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

  @GetMapping(value = "/exception", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Generate dummy exception")
  public ResponseEntity<String> exception() throws FileNotFoundException {
    var seed = ThreadLocalRandom.current().nextInt(10) % 3;
    var e = switch (seed) {
    case 0:
      yield new IllegalArgumentException();
    case 1:
      yield new FileNotFoundException();
    case 2:
      yield new SQLException();
    default:
      yield new NullPointerException();
    };

    // always throw
    if (seed >= 0) {
      throw new FileNotFoundException();
    }

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Generate dummy exception " + e.getClass());
  }

  @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Just a simple hello world")
  public String hello() {
    return String.format("Version [%s] Hello from app [%s] on k8s pod [%s]", ApiResponseFilter.APP_VERSION,
        ApiResponseFilter.getAppIdentifier(), ApiResponseFilter.getPodName());
  }

  @GetMapping(value = "/log", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Log the query param")
  public String log(
      @RequestParam(name = "text", required = true) @Parameter(description = "Text to be logged", example = "Just a simple text") String text) {
    var logText = ApiResponseFilter.getAppIdentifier() + " logging : " + text;
    log.info(logText);

    return logText;
  }

  @GetMapping(value = "/status/{response-status-code}", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Send a process, delayed for random millisecond (0-1000 ms), with certain response status code (2xx, 3xx, 4xx, 5xx)."
      + " Use string <code>random</code> as parameter for generating random response status code.")
  public ResponseEntity<String> status(
      @Pattern(regexp = "2xx|3xx|4xx|5xx|random") @PathVariable(name = "response-status-code", required = true) @Parameter(description = "Requested status code, or string <code>random</code>. Other values will generate response code 500.", example = "4xx", schema = @Schema(type = "string", allowableValues = {
          "random", "2xx", "3xx", "4xx", "5xx" })) String responseStatusCode)
      throws InterruptedException {
    var delayMs = RandomUtils.nextInt(0, 1001);
    TimeUnit.MILLISECONDS.sleep(delayMs);

    var listStatus = MAP_RESPONSE_STATUS_CODE.get(responseStatusCode);
    var status = listStatus.get(RandomUtils.nextInt(0, listStatus.size()));

    return ResponseEntity.status(status).body("Return after " + delayMs + " millisecond delay");
  }

  @GetMapping(value = "/time", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Show current time")
  public HelloTimeResponse time() {
    return HelloTimeResponse.builder().randomNumber(ThreadLocalRandom.current().nextLong())
        .currentTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_TIME)).build();
  }

}