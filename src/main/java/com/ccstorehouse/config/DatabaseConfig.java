package com.ccstorehouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    
    @Value("${DATABASE_URL:jdbc:postgresql://localhost:5432/ccstorehouse}")
    private String databaseUrl;
    
    @Value("${POSTGRES_USER:postgres}")
    private String postgresUser;
    
    @Value("${POSTGRES_PASSWORD:admin}")
    private String postgresPassword;
    
    @Bean
    @Primary
    public DataSource dataSource() {
        // Check if we're using the Render specific PostgreSQL URL
        if (databaseUrl.contains("postgresql://") && !databaseUrl.startsWith("jdbc:")) {
            // For this specific URL: postgresql://ccstorehouse_db_user:nCQnFWEk6FtyFmJrjrhQQrwF1uXTfMEW@dpg-d0fcd8juibrs73ei8vu0-a/ccstorehouse_db
            String[] parts = databaseUrl.split("@");
            
            // Extract credentials
            String credentials = parts[0].replace("postgresql://", "");
            String[] credentialParts = credentials.split(":");
            String username = credentialParts[0];
            String password = credentialParts[1];
            
            // Extract host and database
            String[] hostDbParts = parts[1].split("/");
            String host = hostDbParts[0];
            String database = hostDbParts[1];
            
            // Construct jdbc URL - use port 5432 by default for PostgreSQL
            String jdbcUrl = "jdbc:postgresql://" + host + ":5432/" + database;
            
            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .build();
        } else {
            // It's already a JDBC URL or using the environment variables directly
            return DataSourceBuilder.create()
                    .url(databaseUrl)
                    .username(postgresUser)
                    .password(postgresPassword)
                    .build();
        }
    }
} 