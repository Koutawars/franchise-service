package co.com.bancolombia.model.franchise;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseTest {

  @Test
  void shouldCreateFranchiseWithBuilder() {
    Franchise franchise = Franchise.builder()
        .id("franchise-1")
        .name("Test Franchise")
        .build();

    assertEquals("franchise-1", franchise.getId());
    assertEquals("Test Franchise", franchise.getName());
  }

  @Test
  void shouldCreateFranchiseWithAllArgsConstructor() {
    Franchise franchise = new Franchise("franchise-1", "Test Franchise");

    assertEquals("franchise-1", franchise.getId());
    assertEquals("Test Franchise", franchise.getName());
  }

  @Test
  void shouldUpdateName() {
    Franchise franchise = Franchise.builder()
        .id("franchise-1")
        .name("Original Name")
        .build();

    franchise.setName("Updated Name");

    assertEquals("Updated Name", franchise.getName());
  }

  @Test
  void shouldReturnSameIdAfterUpdate() {
    Franchise franchise = Franchise.builder()
        .id("franchise-1")
        .name("Test Franchise")
        .build();

    franchise.setName("New Name");

    assertEquals("franchise-1", franchise.getId());
  }
}