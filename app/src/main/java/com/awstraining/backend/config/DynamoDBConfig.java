package com.awstraining.backend.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class DynamoDBConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.dynamodb.poolSize:100}")
    private int poolSize;

    // following properties are optional and only needed on local machine when working with localhost
    @Value("${aws.dynamodb.endpoint:#{null}}")
    private String dynamodbEndpoint;

    @Value("${aws.accessKey:#{null}}")
    private String dynamodbAccessKey;

    @Value("${aws.secretKey:#{null}}")
    private String dynamodbSecretKey;

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(buildAmazonDynamoDB());
    }

    private AmazonDynamoDB buildAmazonDynamoDB() {
        // this is the case when using a locally started dynamodb container
        if (dynamodbEndpoint != null && dynamodbAccessKey != null && dynamodbSecretKey != null) {
            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamodbEndpoint, awsRegion))
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(dynamodbAccessKey, dynamodbSecretKey))).build();
        } else {
            // using real dynamodb instance
            return AmazonDynamoDBClientBuilder.standard()
                            .withClientConfiguration(new ClientConfiguration()
                                    .withMaxConnections(poolSize))
                            .build();
        }
    }
}
