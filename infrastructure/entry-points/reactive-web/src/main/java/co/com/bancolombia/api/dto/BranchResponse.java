package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.franchise.Branch;
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
@Schema(description = "Branch response")
public class BranchResponse {
  @Schema(description = "Unique identifier of the branch", example = "456e7890-e89b-12d3-a456-426614174001")
  private String id;
  
  @Schema(description = "Name of the branch", example = "Downtown Branch")
  private String name;
  
  @Schema(description = "Franchise ID this branch belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
  private String franchiseId;

  public static BranchResponse from(Branch branch) {
    return BranchResponse.builder()
        .id(branch.getId())
        .name(branch.getName())
        .franchiseId(branch.getFranchiseId())
        .build();
  }
}