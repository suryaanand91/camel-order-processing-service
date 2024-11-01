package com.samples.order.processing.order.config;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.camel.component.mongodb.MongoDbComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoDbConfig {

    // Injecting the MongoDB URI from application properties
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    // Define a MongoClient bean using the URI
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    // Define the MongoTemplate bean to use MongoClient
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient, 
                                       @Value("${spring.data.mongodb.database}") String databaseName) {
        return new MongoTemplate(mongoClient, databaseName);
    }

    // Configure the Camel MongoDbComponent using MongoClient
    @Bean
    public MongoDbComponent mongoDbComponent(MongoClient mongoClient) {
        MongoDbComponent mongoDbComponent = new MongoDbComponent();
        mongoDbComponent.setMongoConnection(mongoClient);  // Setting MongoClient for Camel
        return mongoDbComponent;
    }
}
