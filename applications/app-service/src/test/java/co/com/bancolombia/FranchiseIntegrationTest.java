package co.com.bancolombia;

import co.com.bancolombia.api.dto.*;
import co.com.bancolombia.config.DynamoDBTestConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
@Import(DynamoDBTestConfig.class)
class FranchiseIntegrationTest {

  @Container
  static LocalStackContainer localstack = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:3.0"))
      .withServices(DYNAMODB);

  @Autowired
  private WebTestClient webTestClient;

  private static String franchiseId;
  private static String branchId;
  private static String productId;

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("aws.dynamodb.endpoint", () -> localstack.getEndpointOverride(DYNAMODB).toString());
    registry.add("aws.region", () -> localstack.getRegion());
    registry.add("aws.accessKeyId", localstack::getAccessKey);
    registry.add("aws.secretAccessKey", localstack::getSecretKey);
  }

  @BeforeAll
  static void setup() {
    DynamoDbClient dynamoDb = DynamoDbClient.builder()
        .endpointOverride(localstack.getEndpointOverride(DYNAMODB))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
        .region(Region.of(localstack.getRegion()))
        .build();

    dynamoDb.createTable(CreateTableRequest.builder()
        .tableName("franchise-test-table")
        .keySchema(
            KeySchemaElement.builder().attributeName("pk").keyType(KeyType.HASH).build(),
            KeySchemaElement.builder().attributeName("sk").keyType(KeyType.RANGE).build())
        .attributeDefinitions(
            AttributeDefinition.builder().attributeName("pk").attributeType(ScalarAttributeType.S).build(),
            AttributeDefinition.builder().attributeName("sk").attributeType(ScalarAttributeType.S).build())
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .build());
  }

  @Test
  @Order(1)
  void shouldCreateFranchise() {
    CreateFranchise request = CreateFranchise.builder().name("Test Franchise").build();

    webTestClient.post()
        .uri("/api/v1/franchises")
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.data.id").value(id -> franchiseId = id.toString());

    Assertions.assertNotNull(franchiseId);
  }

  @Test
  @Order(2)
  void shouldCreateBranch() {
    CreateBranch request = CreateBranch.builder().name("Test Branch").build();

    webTestClient.post()
        .uri("/api/v1/franchises/" + franchiseId + "/branches")
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.data.id").value(id -> branchId = id.toString());

    Assertions.assertNotNull(branchId);
  }

  @Test
  @Order(3)
  void shouldCreateProduct() {
    CreateProduct request = CreateProduct.builder().name("Test Product").stock(100).build();

    webTestClient.post()
        .uri("/api/v1/franchises/" + franchiseId + "/branches/" + branchId + "/products")
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.data.id").value(id -> productId = id.toString());

    Assertions.assertNotNull(productId);
  }

  @Test
  @Order(4)
  void shouldUpdateProductStock() {
    UpdateStock request = UpdateStock.builder().stock(150).build();

    webTestClient.patch()
        .uri("/api/v1/franchises/" + franchiseId + "/branches/" + branchId + "/products/" + productId + "/stock")
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  @Order(5)
  void shouldGetTopProducts() {
    webTestClient.get()
        .uri("/api/v1/franchises/" + franchiseId + "/top-products")
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  @Order(6)
  void shouldUpdateFranchiseName() {
    UpdateName request = UpdateName.builder().name("Updated Franchise").build();

    webTestClient.patch()
        .uri("/api/v1/franchises/" + franchiseId + "/name")
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  @Order(7)
  void shouldDeleteProduct() {
    webTestClient.delete()
        .uri("/api/v1/franchises/" + franchiseId + "/branches/" + branchId + "/products/" + productId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }
}
