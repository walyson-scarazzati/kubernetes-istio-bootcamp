package com.course.devops.yellow.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiResponseFilter extends OncePerRequestFilter {

  public static final String APP_VERSION = "2.0.1";
  private static final String HTTP_HEADER_K8S_APP_VERSION = "K8s-App-Version";
  private static final String HTTP_HEADER_K8S_IDENTIFIER = "K8s-App-Identifier";
  private static final String HTTP_HEADER_K8S_POD = "K8s-Pod-Name";

  @Value("${spring.application.name}")
  private String springApplicationName;
  private static String appIdentifier;
  private static String podName = Optional.ofNullable(System.getenv("HOSTNAME")).orElse("not-kubernetes");

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    response.addHeader(HTTP_HEADER_K8S_APP_VERSION, APP_VERSION);
    response.addHeader(HTTP_HEADER_K8S_IDENTIFIER, appIdentifier);
    response.addHeader(HTTP_HEADER_K8S_POD, podName);

    filterChain.doFilter(request, response);
  }

  @PostConstruct
  private void postConstruct() {
    try {
      ApiResponseFilter.appIdentifier = springApplicationName + " running at "
          + InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      ApiResponseFilter.appIdentifier = springApplicationName + " running at [unknown]";
    }
  }

  public static String getAppIdentifier() {
    return appIdentifier;
  }

  public static String getPodName() {
    return podName;
  }

}
