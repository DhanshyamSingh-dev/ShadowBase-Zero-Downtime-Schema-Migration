
package com.shadowbase.shadowbase.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

@Service
public class SchemaReaderService {

    private final DataSource dataSource;

    public SchemaReaderService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> getTableNames() throws Exception {

        List<String> tables = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet resultSet = metaData.getTables(
                    connection.getCatalog(),
                    null,
                    "%",
                    new String[]{"TABLE"})) {

                while (resultSet.next()) {
                    tables.add(resultSet.getString("TABLE_NAME"));
                }
            }
        }

        return tables;
    }
}
