package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.CreateBranch;
import co.com.bancolombia.api.dto.CreateFranchise;
import co.com.bancolombia.api.dto.CreateProduct;
import co.com.bancolombia.api.dto.UpdateName;
import co.com.bancolombia.api.dto.UpdateStock;
import co.com.bancolombia.api.utils.RequestValidator;
import co.com.bancolombia.api.utils.ResponseUtil;
import co.com.bancolombia.usecase.franchise.BranchUseCase;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import co.com.bancolombia.usecase.franchise.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {
  private static final String FRANCHISE_ID = "franchiseId";
  private static final String BRANCH_ID = "branchId";
  private static final String PRODUCT_ID = "productId";
  
  private final FranchiseUseCase franchiseUseCase;
  private final RequestValidator validator;
  public Mono<ServerResponse> listenPOSTCreateFranchise(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(CreateFranchise.class)
        .flatMap(validator::validate)
        .map(CreateFranchise::toFranchise)
        .flatMap(franchiseUseCase::addFranchise)
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenPATCHUpdateFranchiseName(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    return serverRequest.bodyToMono(UpdateName.class)
        .flatMap(validator::validate)
        .flatMap(dto -> franchiseUseCase.updateNameFranchise(franchiseId, dto.getName()))
        .flatMap(ResponseUtil::processOk);
  }

}
