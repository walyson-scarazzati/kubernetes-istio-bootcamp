package com.course.devops.white.api.response.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper class for JSON element on response body, section
 * <code>header.errors</code>.
 * 
 * @author timpamungkas
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error wrapper for base response (JSON)")
public class JsonBaseError {

  /**
   * Custom error code
   */
  @Schema(description = "Custom error code")
  private String code;

  /**
   * User-friendly error message to be displayed on frontend.
   */
  @Schema(description = "User-friendly error message to be displayed on frontend")
  private String message;

  /**
   * Technical error message for debugging.
   */
  @Schema(description = "Technical error message for debugging")
  private String reason;

}
