package com.speer.notes.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MongoTemplateConfig extends AbstractMongoClientConfiguration {
    Logger logger = LoggerFactory.getLogger(MongoTemplateConfig.class);
    private final String databaseUri;
    private final String databaseName;

    public MongoTemplateConfig(@Value("${mongodb.uri}") String databaseUri,
                               @Value("${mongodb.database}") String databaseName) {
        logger.info("credentials are " + databaseName + " "+databaseUri);
        this.databaseUri = databaseUri;
        this.databaseName = databaseName;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    public @Bean MongoClient mongoClient(){
        return MongoClients.create(databaseUri);
    }

    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate() {
        logger.info("credentials are " + databaseName + " "+databaseUri);
        return new MongoTemplate(MongoClients.create(databaseUri), databaseName);
    }

}
