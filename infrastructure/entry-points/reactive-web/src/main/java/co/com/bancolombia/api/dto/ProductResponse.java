package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.franchise.Product;
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
@Schema(description = "Product response")
public class ProductResponse {
  @Schema(description = "Unique identifier of the product", example = "789e0123-e89b-12d3-a456-426614174002")
  private String id;
  
  @Schema(description = "Name of the product", example = "Big Mac")
  private String name;
  
  @Schema(description = "Current stock quantity", example = "100")
  private Integer stock;
  
  @Schema(description = "Franchise ID this product belongs to", example = "123e4567-e89b-12d3-a456-426614174000")
  private String franchiseId;
  
  @Schema(description = "Branch ID this product belongs to", example = "456e7890-e89b-12d3-a456-426614174001")
  private String branchId;

  public static ProductResponse from(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .stock(product.getStock())
        .franchiseId(product.getFranchiseId())
        .branchId(product.getBranchId())
        .build();
  }
}