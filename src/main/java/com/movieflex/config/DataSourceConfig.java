package com.movieflex.config;

import com.movieflex.dto.DataSourceModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Value("${spring.profiles.active}")
    private String domain;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${project.poster}")
    private String path;

    @Value("${sender.email}")
    private String senderEmail;

    @Bean
    public synchronized DataSourceModel getDataSource() {
        DataSourceModel dataSourceModel = null;
        if (domain.equals("dev")) {
            dataSourceModel = getDevelopmentDataSource();
        } else {
            dataSourceModel = new DataSourceModel();
        }
        return dataSourceModel;
    }

    private DataSourceModel getDevelopmentDataSource() {
        return DataSourceModel.builder()
                .baseUrl(baseUrl)
                .path(path)
                .senderEmail(senderEmail)
                .build();
    }
}
