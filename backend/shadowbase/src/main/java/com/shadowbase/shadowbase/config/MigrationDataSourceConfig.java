
package com.shadowbase.shadowbase.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class MigrationDataSourceConfig {

    @Primary
    @Bean(name = "dataSource")
    public DataSource sourceDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {

        return createDataSource(url, username, password);
    }

    @Bean(name = "migrationDataSource")
    public DataSource migrationDataSource(
            @Value("${shadowbase.migration.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {

        if (!url.endsWith("/shadowbase_migration_test")) {
            throw new IllegalStateException(
                "Migration database must be shadowbase_migration_test"
            );
        }

        return createDataSource(url, username, password);
    }

    private DataSource createDataSource(
            String url, String username, String password) {

        DriverManagerDataSource dataSource =
                new DriverManagerDataSource();

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
