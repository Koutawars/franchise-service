package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.franchise.Franchise;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(description = "Request to create a new franchise")
public class CreateFranchise {
  @Nonnull
  @NotEmpty
  @Schema(description = "Name of the franchise", example = "McDonald's")
  private String name;

  public Franchise toFranchise() {
    return Franchise.builder()
        .name(this.name)
        .build();
  }
}
