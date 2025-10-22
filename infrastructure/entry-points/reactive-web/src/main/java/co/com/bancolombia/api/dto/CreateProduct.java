package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.franchise.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(description = "Request to create a new product")
public class CreateProduct {
  @Nonnull
  @NotEmpty
  @Schema(description = "Name of the product", example = "Big Mac")
  private String name;
  
  @NotNull
  @Schema(description = "Initial stock quantity", example = "100")
  private Integer stock;

  public Product toProduct(String franchiseId, String branchId) {
    return Product.builder()
        .name(this.name)
        .stock(this.stock)
        .franchiseId(franchiseId)
        .branchId(branchId)
        .build();
  }
}