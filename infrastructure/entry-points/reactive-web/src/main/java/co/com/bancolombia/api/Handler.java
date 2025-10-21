package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateBranch;
import co.com.bancolombia.api.dto.CreateFranchise;
import co.com.bancolombia.api.dto.CreateProduct;
import co.com.bancolombia.api.dto.UpdateName;
import co.com.bancolombia.api.dto.UpdateStock;
import co.com.bancolombia.api.utils.RequestValidator;
import co.com.bancolombia.api.utils.ResponseUtil;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
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

  public Mono<ServerResponse> listenPOSTCreateBranch(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    return serverRequest.bodyToMono(CreateBranch.class)
        .flatMap(validator::validate)
        .map(dto -> dto.toBranch(franchiseId))
        .flatMap(franchiseUseCase::addBranch)
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenPOSTCreateProduct(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    return serverRequest.bodyToMono(CreateProduct.class)
        .flatMap(validator::validate)
        .map(dto -> dto.toProduct(franchiseId, branchId))
        .flatMap(franchiseUseCase::addProduct)
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenDELETEProduct(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    String productId = serverRequest.pathVariable(PRODUCT_ID);
    return franchiseUseCase.deleteProduct(productId, branchId, franchiseId)
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenPATCHUpdateStock(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    String productId = serverRequest.pathVariable(PRODUCT_ID);
    return serverRequest.bodyToMono(UpdateStock.class)
        .flatMap(validator::validate)
        .flatMap(dto -> franchiseUseCase.modifyStock(productId, branchId, franchiseId, dto.getStock()))
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenGETTopProducts(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    return franchiseUseCase.getTopProductsPerBranch(franchiseId)
        .collectList()
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenPATCHUpdateFranchiseName(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    return serverRequest.bodyToMono(UpdateName.class)
        .flatMap(validator::validate)
        .flatMap(dto -> franchiseUseCase.updateNameFranchise(franchiseId, dto.getName()))
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenPATCHUpdateBranchName(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    return serverRequest.bodyToMono(UpdateName.class)
        .flatMap(validator::validate)
        .flatMap(dto -> franchiseUseCase.updateNameBranch(branchId, franchiseId, dto.getName()))
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenPATCHUpdateProductName(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    String productId = serverRequest.pathVariable(PRODUCT_ID);
    return serverRequest.bodyToMono(UpdateName.class)
        .flatMap(validator::validate)
        .flatMap(dto -> franchiseUseCase.updateNameProduct(productId, branchId, franchiseId, dto.getName()))
        .flatMap(ResponseUtil::processOk);
  }
}
