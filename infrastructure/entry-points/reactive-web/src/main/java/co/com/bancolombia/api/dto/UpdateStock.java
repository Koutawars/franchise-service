package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update product stock")
public class UpdateStock {
  @NotNull
  @Schema(description = "New stock quantity", example = "50")
  private Integer stock;
}