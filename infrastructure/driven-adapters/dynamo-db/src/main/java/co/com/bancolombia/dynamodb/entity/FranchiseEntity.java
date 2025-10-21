package co.com.bancolombia.dynamodb.entity;


import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Getter
@Setter
@DynamoDbBean
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseEntity {
  private String pk;
  private String sk;
  private String name;
  private String entityType;

  @DynamoDbPartitionKey
  public String getPk() { return pk; }

  @DynamoDbSortKey
  public String getSk() { return sk; }
}
