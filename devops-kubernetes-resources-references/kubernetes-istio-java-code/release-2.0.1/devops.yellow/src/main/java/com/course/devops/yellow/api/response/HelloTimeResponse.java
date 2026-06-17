package com.course.devops.yellow.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelloTimeResponse {

  private long randomNumber;
  private String currentTime;

}
