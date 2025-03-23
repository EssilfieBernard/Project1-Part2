package com.s3rds.ImageToS3RDS;


import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClient;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import static com.amazonaws.regions.Regions.US_EAST_1;
import static com.amazonaws.regions.Regions.US_WEST_2;

@Configuration
public class ApplicationConfiguration {

    @Bean
    @Primary
    public DataSource dataSource() {
        try {
            AWSSecretsManager client = AWSSecretsManagerClient.builder()
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                    .withRegion(US_WEST_2)
                    .build();

            GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
            String secretName = "production/database/credentials";
            getSecretValueRequest.setSecretId(secretName);
            GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);
            String secretValue = getSecretValueResult.getSecretString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode secretJson = mapper.readTree(secretValue);

            String host = secretJson.get("host").asText();
            String port = secretJson.get("port").asText();
            String dbname = secretJson.get("dbname").asText();
            String username = secretJson.get("username").asText();
            String password = secretJson.get("password").asText();

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;

            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve database credentials from Secrets Manager", e);
        }
    }

    @Bean
    public AmazonS3 amazonS3() {

        return AmazonS3Client.builder()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .withRegion(US_EAST_1)
                .build();
    }
}
