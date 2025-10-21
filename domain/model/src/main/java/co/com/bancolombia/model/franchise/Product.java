package co.com.bancolombia.model.franchise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class Product {
  private String id;
  private String name;
  private Integer stock;
  private String franchiseId;
  private String branchId;
}
