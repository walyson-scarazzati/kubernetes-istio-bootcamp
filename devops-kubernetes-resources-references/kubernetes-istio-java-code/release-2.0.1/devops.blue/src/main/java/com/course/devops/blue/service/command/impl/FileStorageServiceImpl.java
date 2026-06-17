package com.course.devops.blue.service.command.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.course.devops.blue.service.command.FileStorageService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

  private Path uploadFolder = Paths.get("upload");

  @Override
  public void init(String root) {
    try {
      if (StringUtils.isNotEmpty(root)) {
        this.uploadFolder = Paths.get(root);
      }
      Files.createDirectories(uploadFolder);
    } catch (IOException e) {
      log.error("Error init storage : {}", e.getMessage());
    }
  }

  @Override
  public String save(MultipartFile f) {
    final var filename = UUID.randomUUID().toString();

    try {
      Files.copy(f.getInputStream(), this.uploadFolder.resolve(filename));
    } catch (IOException e) {
      var errorText = String.format("Error saving {} : {}", f.getOriginalFilename(), e.getMessage());
      log.error(errorText);

      return errorText;
    }

    return filename;
  }

  @Override
  public List<String> list() {
    var allFiles = new ArrayList<String>();

    try {
      Files.list(uploadFolder).forEach(c -> allFiles.add(c.getFileName().toString()));
    } catch (IOException e) {
      log.error("Cannot iterate files : " + e.getMessage());
    }

    return allFiles;
  }

  @Override
  public Resource load(String filename) {
    try {
      var file = uploadFolder.resolve(filename);
      var resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        return null;
      }
    } catch (Exception e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

}
