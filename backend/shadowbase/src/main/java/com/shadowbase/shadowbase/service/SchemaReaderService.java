
package com.shadowbase.shadowbase.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.shadowbase.shadowbase.schema.ColumnInfo;
import com.shadowbase.shadowbase.schema.TableInfo;

@Service
public class SchemaReaderService {

    private final DataSource dataSource;

    public SchemaReaderService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Existing method: retrieves table names
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

    // New method: retrieves tables with column details
    public List<TableInfo> getTables() throws Exception {

        List<TableInfo> tables = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            DatabaseMetaData metaData = connection.getMetaData();

            String catalog = connection.getCatalog();

            try (ResultSet tableResult = metaData.getTables(
                    catalog,
                    null,
                    "%",
                    new String[]{"TABLE"})) {

                while (tableResult.next()) {

                    String tableName = tableResult.getString("TABLE_NAME");

                    List<ColumnInfo> columns = new ArrayList<>();

                    try (ResultSet columnResult = metaData.getColumns(
                            catalog,
                            null,
                            tableName,
                            "%")) {

                        while (columnResult.next()) {

                           
String columnName =
        columnResult.getString("COLUMN_NAME");

String dataType =
        columnResult.getString("TYPE_NAME");

boolean nullable =
        columnResult.getInt("NULLABLE")
        == DatabaseMetaData.columnNullable;

int columnSize =
        columnResult.getInt("COLUMN_SIZE");

Integer length = columnResult.wasNull() ? null : columnSize;

String defaultValue =
        columnResult.getString("COLUMN_DEF");

columns.add(new ColumnInfo(
        columnName,
        dataType,
        nullable,
        length,
        defaultValue
));

                        }
                    }

                    tables.add(new TableInfo(tableName, columns));
                }
            }
        }

        return tables;
    }
}