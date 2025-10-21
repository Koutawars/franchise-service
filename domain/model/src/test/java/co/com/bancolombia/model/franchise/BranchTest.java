package co.com.bancolombia.model.franchise;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {

  @Test
  void shouldCreateBranchWithBuilder() {
    Branch branch = Branch.builder()
        .id("branch-1")
        .name("Test Branch")
        .franchiseId("franchise-1")
        .build();

    assertEquals("branch-1", branch.getId());
    assertEquals("Test Branch", branch.getName());
    assertEquals("franchise-1", branch.getFranchiseId());
  }

  @Test
  void shouldCreateBranchWithAllArgsConstructor() {
    Branch branch = new Branch("branch-1", "Test Branch", "franchise-1");

    assertEquals("branch-1", branch.getId());
    assertEquals("Test Branch", branch.getName());
    assertEquals("franchise-1", branch.getFranchiseId());
  }

  @Test
  void shouldUpdateProperties() {
    Branch branch = Branch.builder()
        .id("branch-1")
        .name("Original Name")
        .franchiseId("franchise-1")
        .build();

    branch.setName("Updated Name");
    branch.setId("new-branch-id");
    branch.setFranchiseId("new-franchise-id");

    assertEquals("new-branch-id", branch.getId());
    assertEquals("Updated Name", branch.getName());
    assertEquals("new-franchise-id", branch.getFranchiseId());
  }
}