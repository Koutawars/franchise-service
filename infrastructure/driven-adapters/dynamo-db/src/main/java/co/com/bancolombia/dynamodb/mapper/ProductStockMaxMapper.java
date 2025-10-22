package co.com.bancolombia.dynamodb.mapper;

import co.com.bancolombia.dynamodb.entity.ProductEntity;
import co.com.bancolombia.dynamodb.entity.ProductStockMaxEntitiy;
import co.com.bancolombia.model.franchise.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductStockMaxMapper {
  public static ProductStockMaxEntitiy toStockMaxEntity(ProductEntity productEntity) {
    String pk = productEntity.getPk();
    String sk = productEntity.getSk();
    String branchId = sk.split("#")[1];
    String productId = sk.split("#PRODUCT#")[1];
    return ProductStockMaxEntitiy.builder()
        .pk(pk)
        .sk("STOCK_MAX#BRANCH#" + branchId)
        .productId("PRODUCT#" + productId)
        .stock(productEntity.getStock())
        .branchId("BRANCH#" + branchId)
        .build();
  }

  public static Product toDomain(ProductStockMaxEntitiy entity, String productName) {
    String franchiseId = entity.getPk().replace("FRANCHISE#", "");
    String branchId = entity.getBranchId().replace("BRANCH#", "");
    String productId = entity.getProductId().replace("PRODUCT#", "");
    return Product.builder()
        .id(productId)
        .name(productName)
        .stock(entity.getStock())
        .branchId(branchId)
        .franchiseId(franchiseId)
        .build();
  }
}
