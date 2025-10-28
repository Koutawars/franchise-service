package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.CreateBranch;
import co.com.bancolombia.api.dto.UpdateName;
import co.com.bancolombia.api.utils.RequestValidator;
import co.com.bancolombia.api.utils.ResponseUtil;
import co.com.bancolombia.usecase.franchise.BranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {
  private final BranchUseCase branchUseCase;
  private final RequestValidator validator;

  private static final String FRANCHISE_ID = "franchiseId";
  private static final String BRANCH_ID = "branchId";

  public Mono<ServerResponse> listenPOSTCreateBranch(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    return serverRequest.bodyToMono(CreateBranch.class)
        .flatMap(validator::validate)
        .map(dto -> dto.toBranch(franchiseId))
        .flatMap(branchUseCase::addBranch)
        .flatMap(ResponseUtil::processOk);
  }

  public Mono<ServerResponse> listenPATCHUpdateBranchName(ServerRequest serverRequest) {
    String franchiseId = serverRequest.pathVariable(FRANCHISE_ID);
    String branchId = serverRequest.pathVariable(BRANCH_ID);
    return serverRequest.bodyToMono(UpdateName.class)
        .flatMap(validator::validate)
        .flatMap(dto -> branchUseCase.updateNameBranch(branchId, franchiseId, dto.getName()))
        .flatMap(ResponseUtil::processOk);
  }
}
