package co.com.bancolombia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@TestConfiguration
public class DynamoDBTestConfig {

  @Bean
  @Primary
  public DynamoDbAsyncClient dynamoDbAsyncClient(
      @Value("${aws.dynamodb.endpoint}") String endpoint,
      @Value("${aws.region}") String region,
      @Value("${aws.accessKeyId}") String accessKey,
      @Value("${aws.secretAccessKey}") String secretKey) {

    return DynamoDbAsyncClient.builder()
        .endpointOverride(URI.create(endpoint))
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }
}
