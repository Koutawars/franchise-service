package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.CreateProduct;
import co.com.bancolombia.api.dto.UpdateName;
import co.com.bancolombia.api.dto.UpdateStock;
import co.com.bancolombia.api.utils.RequestValidator;
import co.com.bancolombia.api.utils.ResponseUtil;
import co.com.bancolombia.usecase.franchise.ProductUseCase;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {
  private final ProductUseCase productUseCase;
  private final RequestValidator validator;
  private static final String FRANCHISE_ID = "franchiseId";
  private static final String BRANCH_ID = "branchId";
  private static final String PRODUCT_ID = "productId";

  public Mono<ServerResponse> listenPOSTCreateProduct(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    return serverRequest.bodyToMono(CreateProduct.class)
        .flatMap(validator::validate)
        .map(dto -> dto.toProduct(franchiseId, branchId))
        .flatMap(productUseCase::addProduct)
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenDELETEProduct(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    String productId = serverRequest.pathVariable(PRODUCT_ID);
    return productUseCase.deleteProduct(productId, branchId, franchiseId)
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenPATCHUpdateStock(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    String productId = serverRequest.pathVariable(PRODUCT_ID);
    return serverRequest.bodyToMono(UpdateStock.class)
        .flatMap(validator::validate)
        .flatMap(dto -> productUseCase.modifyStock(productId, branchId, franchiseId, dto.getStock()))
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenGETTopProducts(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    return productUseCase.getTopProductsPerBranch(franchiseId)
        .collectList()
        .flatMap(ResponseUtil::processOk);
  }


  public Mono<ServerResponse> listenPATCHUpdateProductName(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    String productId = serverRequest.pathVariable(PRODUCT_ID);
    return serverRequest.bodyToMono(UpdateName.class)
        .flatMap(validator::validate)
        .flatMap(dto -> productUseCase.updateNameProduct(productId, branchId, franchiseId, dto.getName()))
        .flatMap(ResponseUtil::processOk);
  }
}
