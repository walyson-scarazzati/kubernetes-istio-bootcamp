package com.course.devops.blue.service.command;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  void init(String root);

  String save(MultipartFile f);

  List<String> list();

  Resource load(String filename);
}
