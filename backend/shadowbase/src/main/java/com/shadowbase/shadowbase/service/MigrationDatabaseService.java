
package com.shadowbase.shadowbase.service;

import java.sql.Connection;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MigrationDatabaseService {

    private final DataSource migrationDataSource;

    public MigrationDatabaseService(
            @Qualifier("migrationDataSource")
            DataSource migrationDataSource) {

        this.migrationDataSource = migrationDataSource;
    }

    public String getDatabaseName() throws Exception {

        try (Connection connection =
                migrationDataSource.getConnection()) {

            return connection.getCatalog();
        }
    }
}
