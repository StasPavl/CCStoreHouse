package com.ccstorehouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

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
    public DataSource dataSource() throws URISyntaxException {
        if (!databaseUrl.startsWith("jdbc:")) {
            // If it's a Heroku/Render style URL (postgres://), convert it to JDBC format
            URI dbUri = new URI(databaseUrl);
            
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();
            
            return DataSourceBuilder.create()
                    .url(dbUrl)
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