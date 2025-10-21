package co.com.bancolombia.dynamodb.mapper;

import co.com.bancolombia.dynamodb.entity.FranchiseEntity;
import co.com.bancolombia.model.franchise.Franchise;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class FranchiseMapper {
  public static FranchiseEntity toEntity(Franchise franchise) {
    String id = franchise.getId() != null ? franchise.getId() : UUID.randomUUID().toString();

    return FranchiseEntity.builder()
        .pk("FRANCHISE#" + id)
        .sk("METADATA")
        .entityType("Franchise")
        .name(franchise.getName())
        .build();
  }

  public static Franchise toDomain(FranchiseEntity entity) {
    return Franchise.builder()
        .id(entity.getPk().replace("FRANCHISE#", ""))
        .name(entity.getName())
        .build();
  }
}
