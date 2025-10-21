package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Standard API response wrapper")
public class StandardResponse<T> {
  @Schema(description = "Response code", example = "200")
  String responseCode;
  
  @Schema(description = "Response message", example = "Success")
  String message;
  
  @Schema(description = "Response timestamp", example = "2024-01-01T10:00:00Z")
  String timestamp;
  
  @Schema(description = "Response data")
  T data;
}