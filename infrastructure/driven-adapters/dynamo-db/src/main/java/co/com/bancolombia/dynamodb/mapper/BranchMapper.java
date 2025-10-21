package co.com.bancolombia.dynamodb.mapper;

import co.com.bancolombia.dynamodb.entity.BranchEntity;
import co.com.bancolombia.model.franchise.Branch;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class BranchMapper {
  public static BranchEntity toEntity(Branch branch) {
    String id = branch.getId() != null ? branch.getId() : UUID.randomUUID().toString();

    return BranchEntity.builder()
        .pk("FRANCHISE#" + branch.getFranchiseId())
        .sk("BRANCH#" + id)
        .entityType("Branch")
        .name(branch.getName())
        .build();
  }

  public static Branch toDomain(BranchEntity entity) {
    return Branch.builder()
        .id(entity.getSk().replace("BRANCH#", ""))
        .franchiseId(entity.getPk().replace("FRANCHISE#", ""))
        .name(entity.getName())
        .build();
  }
}
