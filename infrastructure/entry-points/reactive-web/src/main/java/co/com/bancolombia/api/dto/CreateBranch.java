package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.franchise.Branch;
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
@Schema(description = "Request to create a new branch")
public class CreateBranch {
  @Nonnull
  @NotEmpty
  @Schema(description = "Name of the branch", example = "Downtown Branch", required = true)
  private String name;

  public Branch toBranch(String franchiseId) {
    return Branch.builder()
        .name(this.name)
        .franchiseId(franchiseId)
        .build();
  }
}