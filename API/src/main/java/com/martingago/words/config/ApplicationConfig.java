package com.martingago.words.config;

import com.zaxxer.hikari.util.DriverDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfig {

    @Bean
    public DataSource dataSource(){
        Dotenv dotenv = Dotenv.load();
        DriverManagerDataSource driverManager = new DriverManagerDataSource();
        driverManager.setDriverClassName("com.mysql.jdbc.Driver");
        driverManager.setUrl("jdbc:mysql://localhost:3306/api_words");
        driverManager.setUsername(dotenv.get("DB_USER"));
        driverManager.setPassword(dotenv.get("DB_PASSWORD"));
        return driverManager;
    };


}
