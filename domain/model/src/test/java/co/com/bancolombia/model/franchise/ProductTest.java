package co.com.bancolombia.model.franchise;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

  @Test
  void shouldCreateProductWithBuilder() {
    Product product = Product.builder()
        .id("product-1")
        .name("Test Product")
        .stock(10)
        .franchiseId("franchise-1")
        .branchId("branch-1")
        .build();

    assertEquals("product-1", product.getId());
    assertEquals("Test Product", product.getName());
    assertEquals(10, product.getStock());
    assertEquals("franchise-1", product.getFranchiseId());
    assertEquals("branch-1", product.getBranchId());
  }

  @Test
  void shouldCreateProductWithAllArgsConstructor() {
    Product product = new Product("product-1", "Test Product", 10, "franchise-1", "branch-1");

    assertEquals("product-1", product.getId());
    assertEquals("Test Product", product.getName());
    assertEquals(10, product.getStock());
    assertEquals("franchise-1", product.getFranchiseId());
    assertEquals("branch-1", product.getBranchId());
  }

  @Test
  void shouldUpdateAllProperties() {
    Product product = Product.builder()
        .id("product-1")
        .name("Original Product")
        .stock(5)
        .franchiseId("franchise-1")
        .branchId("branch-1")
        .build();

    product.setId("new-product-id");
    product.setName("Updated Product");
    product.setStock(20);
    product.setFranchiseId("new-franchise-id");
    product.setBranchId("new-branch-id");

    assertEquals("new-product-id", product.getId());
    assertEquals("Updated Product", product.getName());
    assertEquals(20, product.getStock());
    assertEquals("new-franchise-id", product.getFranchiseId());
    assertEquals("new-branch-id", product.getBranchId());
  }

  @Test
  void shouldHandleZeroStock() {
    Product product = Product.builder()
        .id("product-1")
        .name("Test Product")
        .stock(0)
        .franchiseId("franchise-1")
        .branchId("branch-1")
        .build();

    assertEquals(0, product.getStock());
  }
}