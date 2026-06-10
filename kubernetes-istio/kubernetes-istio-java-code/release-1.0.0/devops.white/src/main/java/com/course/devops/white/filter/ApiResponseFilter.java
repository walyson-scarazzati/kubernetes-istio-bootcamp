package com.course.devops.white.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiResponseFilter extends OncePerRequestFilter {

  public static final String APP_VERSION = "1.0.0";
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
