package co.com.bancolombia.dynamodb.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Getter
@Setter
@DynamoDbBean
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
  private String pk;
  private String sk;
  private String name;
  private String entityType;
  private Integer stock;
  private String branchProductsKey;
  private String stockSortKey;

  @DynamoDbPartitionKey
  public String getPk() { return pk; }

  @DynamoDbSortKey
  public String getSk() { return sk; }

  @DynamoDbSecondaryPartitionKey(indexNames = "BranchProductsByStock")
  public String getBranchProductsKey() { return branchProductsKey; }

  @DynamoDbSecondarySortKey(indexNames = "BranchProductsByStock")
  public String getStockSortKey() { return stockSortKey; }
}
