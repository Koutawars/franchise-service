package co.com.bancolombia.dynamodb.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Getter
@Setter
@DynamoDbBean
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockMaxEntitiy {
  private String pk;
  private String sk;
  private String productId;
  private Integer stock;
  private String branchId;

  @DynamoDbPartitionKey
  public String getPk() { return pk; }

  @DynamoDbSortKey
  public String getSk() { return sk; }

  @DynamoDbAttribute("productId")
  public String getProductId() { return productId; }

  @DynamoDbAttribute("stock")
  public Integer getStock() { return stock; }

  @DynamoDbAttribute("branchId")
  public String getBranchId() { return branchId; }
}
