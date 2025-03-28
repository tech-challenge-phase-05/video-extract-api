package br.com.videoextractor.adapters.SQS


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class SQSConfig {
    @Bean
    fun sqsClient(): SqsClient {
        return  SqsClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }
}