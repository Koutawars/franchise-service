package co.com.bancolombia.dynamodb.cache;

import co.com.bancolombia.model.franchise.Franchise;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class FranchiseCache {
  private final Cache<String, Franchise> franchiseCache = Caffeine.newBuilder()
      .expireAfterWrite(Duration.ofMinutes(10))
      .maximumSize(500)
      .build();

  public Franchise get(String franchiseId) {
    return franchiseCache.getIfPresent(franchiseId);
  }

  public void put(String franchiseId, Franchise franchise) {
    franchiseCache.put(franchiseId, franchise);
  }
}
