package co.com.bancolombia.dynamodb.mapper;

import co.com.bancolombia.dynamodb.entity.ProductEntity;
import co.com.bancolombia.model.franchise.Product;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ProductMapper {

  public static final String BRANCH = "BRANCH#";
  public static final String FRANCHISE = "FRANCHISE#";

  public static ProductEntity toEntity(Product product) {
    String id = product.getId() != null ? product.getId() : UUID.randomUUID().toString();

    return ProductEntity.builder()
        .pk(FRANCHISE + product.getFranchiseId())
        .sk(BRANCH + product.getBranchId() + "#PRODUCT#" + id)
        .entityType("Product")
        .name(product.getName())
        .stock(product.getStock())
        .build();
  }

  public static Product toDomain(ProductEntity entity) {
    String franchiseId = entity.getPk().replace(FRANCHISE, "");
    String sk = entity.getSk();

    String branchId = sk.split("#")[1];
    String productId = sk.split("#PRODUCT#")[1];

    return Product.builder()
        .id(productId)
        .franchiseId(franchiseId)
        .branchId(branchId)
        .name(entity.getName())
        .stock(entity.getStock())
        .build();
  }
}
