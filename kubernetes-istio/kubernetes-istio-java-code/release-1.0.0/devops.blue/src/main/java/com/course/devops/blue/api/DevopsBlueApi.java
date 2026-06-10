package com.course.devops.blue.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.course.devops.blue.api.response.HelloTimeResponse;
import com.course.devops.blue.constant.DevopsApiConstants;
import com.course.devops.blue.filter.ApiResponseFilter;
import com.course.devops.blue.load.CpuLoadThread;
import com.course.devops.blue.load.MemoryLoadThread;
import com.course.devops.blue.service.command.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Tag(name = "DevOps-Blue custom API", description = "DevOps-Blue custom API")
@Slf4j
@Validated
public class DevopsBlueApi {

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

  @Autowired
  private FileStorageService docStorageService;

  @Autowired
  private FileStorageService imageStorageService;

  private final Tika tika = new Tika();

  @GetMapping(value = "/delay/{delay-second}", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Send a process, delayed for x second")
  public String delay(
      @Valid @Min(0) @Max(30) @PathVariable(name = "delay-second", required = true) @Parameter(description = "Delay time (in second)", example = "10") long delaySecond)
      throws InterruptedException {
    log.info("Calling delay");
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
    log.info("Calling delay");
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

  @GetMapping(value = "/exception", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Generate dummy exception")
  public ResponseEntity<String> exception() throws FileNotFoundException {
    log.info("Calling exception");
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

  @GetMapping(value = "/load/cpu", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Generate fake CPU load for n seconds (call is non-blocking)")
  public String fakeLoadCpu(@RequestParam(name = "cpu", required = true, defaultValue = "1") int cpu,
      @RequestParam(name = "period-second", required = true, defaultValue = "0") int periodSecond) {
    log.info("Calling load/cpu");
    var executors = Executors.newFixedThreadPool(cpu);

    for (int i = 0; i < cpu; i++) {
      executors.execute(new CpuLoadThread("fake-cpu-" + i, periodSecond));
    }

    return String.format("Starting fake load over period %d second, cpu thread %d", periodSecond, cpu);
  }

  @GetMapping(value = "/load/memory", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Generate fake memory load for n seconds (call is non-blocking)")
  public String fakeLoadMemory(@RequestParam(name = "memory-mb", required = true, defaultValue = "0") int memory,
      @RequestParam(name = "period-second", required = true, defaultValue = "0") int periodSecond) {
    log.info("Calling load/memory");
    Executors.newSingleThreadExecutor().execute(new MemoryLoadThread("fake-memory", memory, periodSecond));

    return String.format("Starting fake load over period %d second, memory %d MB", periodSecond, memory);
  }

  @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Just a simple hello world")
  public String hello() {
    log.info("Calling hello " +ApiResponseFilter.getAppIdentifier() );
    return String.format("Version [%s] Hello from app [%s] on k8s pod [%s]", ApiResponseFilter.APP_VERSION,
        ApiResponseFilter.getAppIdentifier(), ApiResponseFilter.getPodName());
  }

  @GetMapping(value = "/docs", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List existing doc uuid")
  public List<String> listDocs() {
    log.info("Calling docs");
    return docStorageService.list();
  }

  @GetMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "List existing image uuid")
  public List<String> listImages() {
    log.info("Calling images");
    return imageStorageService.list();
  }

  @GetMapping(value = "/image/{image-id}", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Load existing image")
  @ApiResponses(value = { @ApiResponse(responseCode = "404", description = "Image id not found"),
      @ApiResponse(responseCode = "200", description = "Image", content = {
          @Content(mediaType = MediaType.IMAGE_PNG_VALUE), @Content(mediaType = MediaType.IMAGE_JPEG_VALUE),
          @Content(mediaType = MediaType.IMAGE_GIF_VALUE) })

  })
  public ResponseEntity<Resource> loadImage(
      @PathVariable(name = "image-id", required = true) @Parameter(description = "Image uuid to be loaded") String imageId)
      throws IOException {
    log.info("Calling image/id");
    var file = imageStorageService.load(imageId);

    if (file != null) {
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
          .header(HttpHeaders.CONTENT_TYPE, tika.detect(file.getInputStream())).body(file);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping(value = "/doc/{doc-id}", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Load existing doc")
  @ApiResponses(value = { @ApiResponse(responseCode = "404", description = "Document id not found"),
      @ApiResponse(responseCode = "200", description = "Non-image document", content = {
          @Content(mediaType = MediaType.APPLICATION_PDF_VALUE) }) })
  public ResponseEntity<Resource> loadPdf(
      @PathVariable(name = "doc-id", required = true) @Parameter(description = "Document uuid to be loaded") String imageId)
      throws IOException {
    log.info("Calling doc/id");
    var file = docStorageService.load(imageId);

    if (file != null) {
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
          .header(HttpHeaders.CONTENT_TYPE, tika.detect(file.getInputStream())).body(file);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping(value = "/log", produces = MediaType.TEXT_PLAIN_VALUE)
  @Operation(summary = "Log the query param")
  public String log(
      @RequestParam(name = "text", required = true) @Parameter(description = "Text to be logged", example = "Just a simple text") String text) {
    log.info("Calling log");
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
    log.info("Calling status");
    var delayMs = RandomUtils.nextInt(0, 1001);
    TimeUnit.MILLISECONDS.sleep(delayMs);

    var listStatus = MAP_RESPONSE_STATUS_CODE.get(responseStatusCode);
    var status = listStatus.get(RandomUtils.nextInt(0, listStatus.size()));

    return ResponseEntity.status(status).body("Return after " + delayMs + " millisecond delay");
  }

  @GetMapping(value = "/time", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Show current time")
  public HelloTimeResponse time() {
    log.info("Calling time");
    return HelloTimeResponse.builder().randomNumber(ThreadLocalRandom.current().nextLong())
        .currentTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_TIME)).build();
  }

  @PostMapping(value = "/doc", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload document")
  @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Document saved"),
      @ApiResponse(responseCode = "400", description = "Input is image") })
  public ResponseEntity<String> uploadDoc(@RequestPart(name = "file", required = true) MultipartFile file)
      throws IOException {
    log.info("Calling doc");
    if (StringUtils.startsWithIgnoreCase(tika.detect(file.getBytes()), "image")) {
      return ResponseEntity.badRequest().body("Use endpoint /image to upload image");
    }

    var savedFilename = docStorageService.save(file);

    return ResponseEntity.status(HttpStatus.CREATED).body("Saved : " + savedFilename);
  }

  @PostMapping(value = "/image", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload image (jpg, png, gif,...)")
  @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Image saved"),
      @ApiResponse(responseCode = "400", description = "Input is not an image") })
  public ResponseEntity<String> uploadImage(@RequestPart(name = "file", required = true) MultipartFile file)
      throws IOException {
    log.info("Calling image");
    if (!StringUtils.startsWithIgnoreCase(tika.detect(file.getBytes()), "image")) {
      return ResponseEntity.badRequest().body("Not an image (jpg / png / gif / ...)");
    }

    var savedFilename = imageStorageService.save(file);

    return ResponseEntity.status(HttpStatus.CREATED).body("Saved : " + savedFilename);
  }

}