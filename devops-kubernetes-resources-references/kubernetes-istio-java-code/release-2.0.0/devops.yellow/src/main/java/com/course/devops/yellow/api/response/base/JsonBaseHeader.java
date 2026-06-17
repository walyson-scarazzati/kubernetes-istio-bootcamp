package com.course.devops.yellow.api.response.base;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper class for JSON element on response body, section <code>header</code>.
 * 
 * @author timpamungkas
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Base response (JSON). " + "<code>header</code> contains metadata. "
    + "Name is using <code>header</code> for backward-compatibility with BFI standard.")
public class JsonBaseHeader {

  @Builder.Default
  @Schema(description = "List of errors")
  private List<JsonBaseError> errors = new ArrayList<>();

  /**
   * Process time to generate the entire response
   */
  @Schema(description = "Process time to generate the entire response")
  private long processTime;

  /**
   * <code>true</code> if your API response is returning HTTP status response 2xx,
   * <code>false</code> otherwise.
   */
  @Schema(description = "<code>true</code> if your API response is returning HTTP status response 2xx, "
      + "<code>false</code> otherwise")
  private boolean success;

  public void addError(JsonBaseError error) {
    this.errors.add(error);
  }

}
