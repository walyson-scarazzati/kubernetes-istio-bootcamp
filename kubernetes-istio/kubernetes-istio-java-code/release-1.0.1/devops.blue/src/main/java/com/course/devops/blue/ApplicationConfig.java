package com.course.devops.blue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.course.devops.blue.service.command.FileStorageService;
import com.course.devops.blue.service.command.impl.FileStorageServiceImpl;

@Configuration
public class ApplicationConfig {

  @Bean
  public FileStorageService imageStorageService() {
    var fss = new FileStorageServiceImpl();
    fss.init("upload/image");

    return fss;
  }

  @Bean
  public FileStorageService docStorageService() {
    var fss = new FileStorageServiceImpl();
    fss.init("upload/doc");

    return fss;
  }

}
