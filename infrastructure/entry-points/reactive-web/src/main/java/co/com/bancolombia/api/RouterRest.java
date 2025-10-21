package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
  
  @Bean
  @RouterOperations({
      @RouterOperation(
          path = "/api/v1/franchises",
          method = RequestMethod.POST,
          beanClass = Handler.class,
          beanMethod = "listenPOSTCreateFranchise",
          operation = @Operation(
              operationId = "createFranchise",
              summary = "Create a new franchise",
              tags = {"Franchise Management"},
              requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateFranchise.class))),
              responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StandardResponse.class)))}
          )
      ),
      @RouterOperation(
          path = "/api/v1/franchises/{franchiseId}/branches",
          method = RequestMethod.POST,
          beanClass = Handler.class,
          beanMethod = "listenPOSTCreateBranch",
          operation = @Operation(
              operationId = "createBranch",
              summary = "Create a new branch",
              tags = {"Branch Management"},
              parameters = {@Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true)},
              requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateBranch.class))),
              responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StandardResponse.class)))}
          )
      ),
      @RouterOperation(
          path = "/api/v1/franchises/{franchiseId}/branches/{branchId}/products",
          method = RequestMethod.POST,
          beanClass = Handler.class,
          beanMethod = "listenPOSTCreateProduct",
          operation = @Operation(
              operationId = "createProduct",
              summary = "Create a new product",
              tags = {"Product Management"},
              parameters = {
                  @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                  @Parameter(name = "branchId", in = ParameterIn.PATH, required = true)
              },
              requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateProduct.class))),
              responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StandardResponse.class)))}
          )
      ),
      @RouterOperation(
          path = "/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}",
          method = RequestMethod.DELETE,
          beanClass = Handler.class,
          beanMethod = "listenDELETEProduct",
          operation = @Operation(
              operationId = "deleteProduct",
              summary = "Delete a product",
              tags = {"Product Management"},
              parameters = {
                  @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                  @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                  @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
              },
              responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StandardResponse.class)))}
          )
      ),
      @RouterOperation(
          path = "/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock",
          method = RequestMethod.PATCH,
          beanClass = Handler.class,
          beanMethod = "listenPATCHUpdateStock",
          operation = @Operation(
              operationId = "updateStock",
              summary = "Update product stock",
              tags = {"Product Management"},
              parameters = {
                  @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                  @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                  @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
              },
              requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateStock.class))),
              responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StandardResponse.class)))}
          )
      ),
      @RouterOperation(
          path = "/api/v1/franchises/{franchiseId}/top-products",
          method = RequestMethod.GET,
          beanClass = Handler.class,
          beanMethod = "listenGETTopProducts",
          operation = @Operation(
              operationId = "getTopProducts",
              summary = "Get top products per branch",
              tags = {"Product Management"},
              parameters = {@Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true)},
              responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StandardResponse.class)))}
          )
      ),
      @RouterOperation(
          path = "/api/v1/franchises/{franchiseId}/name",
          method = RequestMethod.PATCH,
          beanClass = Handler.class,
          beanMethod = "listenPATCHUpdateFranchiseName",
          operation = @Operation(
              operationId = "updateFranchiseName",
              summary = "Update franchise name",
              tags = {"Franchise Management"},
              parameters = {@Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true)},
              requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateName.class))),
              responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StandardResponse.class)))}
          )
      ),
      @RouterOperation(
          path = "/api/v1/franchises/{franchiseId}/branches/{branchId}/name",
          method = RequestMethod.PATCH,
          beanClass = Handler.class,
          beanMethod = "listenPATCHUpdateBranchName",
          operation = @Operation(
              operationId = "updateBranchName",
              summary = "Update branch name",
              tags = {"Branch Management"},
              parameters = {
                  @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                  @Parameter(name = "branchId", in = ParameterIn.PATH, required = true)
              },
              requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateName.class))),
              responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StandardResponse.class)))}
          )
      ),
      @RouterOperation(
          path = "/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name",
          method = RequestMethod.PATCH,
          beanClass = Handler.class,
          beanMethod = "listenPATCHUpdateProductName",
          operation = @Operation(
              operationId = "updateProductName",
              summary = "Update product name",
              tags = {"Product Management"},
              parameters = {
                  @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                  @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                  @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
              },
              requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateName.class))),
              responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StandardResponse.class)))}
          )
      )
  })
  public RouterFunction<ServerResponse> routerFunction(Handler handler) {
    return nest(path("/api/v1/franchises"),
        route(POST(""), handler::listenPOSTCreateFranchise)
            .andRoute(POST("/{franchiseId}/branches"), handler::listenPOSTCreateBranch)
            .andRoute(POST("/{franchiseId}/branches/{branchId}/products"), handler::listenPOSTCreateProduct)
            .andRoute(DELETE("/{franchiseId}/branches/{branchId}/products/{productId}"), handler::listenDELETEProduct)
            .andRoute(PATCH("/{franchiseId}/branches/{branchId}/products/{productId}/stock"), handler::listenPATCHUpdateStock)
            .andRoute(GET("/{franchiseId}/top-products"), handler::listenGETTopProducts)
            .andRoute(PATCH("/{franchiseId}/name"), handler::listenPATCHUpdateFranchiseName)
            .andRoute(PATCH("/{franchiseId}/branches/{branchId}/name"), handler::listenPATCHUpdateBranchName)
            .andRoute(PATCH("/{franchiseId}/branches/{branchId}/products/{productId}/name"), handler::listenPATCHUpdateProductName));
  }
}
