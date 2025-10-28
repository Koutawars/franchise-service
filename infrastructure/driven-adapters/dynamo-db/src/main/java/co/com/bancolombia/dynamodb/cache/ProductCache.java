package co.com.bancolombia.dynamodb.cache;

import co.com.bancolombia.model.franchise.Product;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class ProductCache {
  private final Cache<String, List<Product>> topProductsByFranchiseCache = Caffeine.newBuilder()
      .expireAfterWrite(Duration.ofMinutes(10))
      .maximumSize(500)
      .build();

  public List<Product> getTop(String franchiseId) {
    return topProductsByFranchiseCache.getIfPresent(franchiseId);
  }

  public void putTop(String franchiseId, List<Product> products) {
    topProductsByFranchiseCache.put(franchiseId, products);
  }
}