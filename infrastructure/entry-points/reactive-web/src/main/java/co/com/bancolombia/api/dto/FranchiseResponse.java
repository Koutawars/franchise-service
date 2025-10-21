package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.franchise.Franchise;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Franchise response")
public class FranchiseResponse {
  @Schema(description = "Unique identifier of the franchise", example = "123e4567-e89b-12d3-a456-426614174000")
  private String id;
  
  @Schema(description = "Name of the franchise", example = "McDonald's")
  private String name;

  public static FranchiseResponse from(Franchise franchise) {
    return FranchiseResponse.builder()
        .id(franchise.getId())
        .name(franchise.getName())
        .build();
  }
}