package com.course.devops.white.api.response.base;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination wrapper
 * 
 * @author timpamungkas
 *
 * @param <T> data type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pagination wrapper for base JSON response")
public class JsonBasePage<T> {

  @Schema(description = "size of record per page")
  private int size;

  @Schema(description = "page number (1 based)")
  private int page;

  @Schema(description = "total pages")
  private int totalPage;

  @Schema(description = "total records")
  private long totalRecords;

  @Schema(description = "actual data")
  private List<T> records;

}
