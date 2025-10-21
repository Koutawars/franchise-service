package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void createFranchise_ToFranchise() {
        CreateFranchise dto = CreateFranchise.builder()
                .name("Test Franchise")
                .build();

        Franchise franchise = dto.toFranchise();

        assertThat(franchise.getName()).isEqualTo("Test Franchise");
        assertThat(franchise.getId()).isNull();
    }

    @Test
    void createBranch_ToBranch() {
        CreateBranch dto = CreateBranch.builder()
                .name("Test Branch")
                .build();

        Branch branch = dto.toBranch("franchise-1");

        assertThat(branch.getName()).isEqualTo("Test Branch");
        assertThat(branch.getFranchiseId()).isEqualTo("franchise-1");
        assertThat(branch.getId()).isNull();
    }

    @Test
    void createProduct_ToProduct() {
        CreateProduct dto = CreateProduct.builder()
                .name("Test Product")
                .stock(50)
                .build();

        Product product = dto.toProduct("franchise-1", "branch-1");

        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getStock()).isEqualTo(50);
        assertThat(product.getFranchiseId()).isEqualTo("franchise-1");
        assertThat(product.getBranchId()).isEqualTo("branch-1");
        assertThat(product.getId()).isNull();
    }

    @Test
    void updateName_GettersAndSetters() {
        UpdateName dto = new UpdateName();
        dto.setName("Updated Name");

        assertThat(dto.getName()).isEqualTo("Updated Name");
    }

    @Test
    void updateStock_GettersAndSetters() {
        UpdateStock dto = new UpdateStock();
        dto.setStock(100);

        assertThat(dto.getStock()).isEqualTo(100);
    }

    @Test
    void standardResponse_Builder() {
        StandardResponse<String> response = StandardResponse.<String>builder()
                .responseCode("SUCCESS")
                .message("Operation completed")
                .data("test-data")
                .timestamp("2024-01-01T10:00:00Z")
                .build();

        assertThat(response.getResponseCode()).isEqualTo("SUCCESS");
        assertThat(response.getMessage()).isEqualTo("Operation completed");
        assertThat(response.getData()).isEqualTo("test-data");
        assertThat(response.getTimestamp()).isEqualTo("2024-01-01T10:00:00Z");
    }
}