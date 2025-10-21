package co.com.bancolombia.config;

import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.utils.Logger;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UseCasesConfigTest {

    @Mock
    private FranchiseRepository franchiseRepository;
    
    @Mock
    private Logger logger;

    @Test
    void getFranchiseUseCase_ShouldCreateInstance() {
        UseCasesConfig config = new UseCasesConfig();
        
        FranchiseUseCase useCase = config.getFranchiseUseCase(franchiseRepository, logger);
        
        assertThat(useCase).isNotNull();
    }
}