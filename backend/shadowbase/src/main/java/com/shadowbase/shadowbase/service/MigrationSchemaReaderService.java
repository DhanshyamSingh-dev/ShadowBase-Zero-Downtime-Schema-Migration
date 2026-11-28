
package com.shadowbase.shadowbase.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.shadowbase.shadowbase.schema.ColumnInfo;
import com.shadowbase.shadowbase.schema.TableInfo;

@Service
public class MigrationSchemaReaderService {

    private final DataSource migrationDataSource;

    public MigrationSchemaReaderService(
            @Qualifier("migrationDataSource")
            DataSource migrationDataSource) {

        this.migrationDataSource = migrationDataSource;
    }

    public List<TableInfo> getTables() throws Exception {

        List<TableInfo> tables = new ArrayList<>();

        try (Connection connection =
                     migrationDataSource.getConnection()) {

            if (!"shadowbase_migration_test".equals(
                    connection.getCatalog())) {
                throw new IllegalStateException(
                        "Unsafe migration database connection."
                );
            }

            DatabaseMetaData metaData =
                    connection.getMetaData();

            try (ResultSet tableResults =
                         metaData.getTables(
                                 connection.getCatalog(),
                                 null,
                                 "%",
                                 new String[]{"TABLE"})) {

                while (tableResults.next()) {

                    String tableName =
                            tableResults.getString("TABLE_NAME");

                    List<ColumnInfo> columns =
                            new ArrayList<>();

                    try (ResultSet columnResults =
                                 metaData.getColumns(
                                         connection.getCatalog(),
                                         null,
                                         tableName,
                                         "%")) {

                        while (columnResults.next()) {

                            String columnName =
                                    columnResults.getString("COLUMN_NAME");

                            String dataType =
                                    columnResults.getString("TYPE_NAME");

                            int columnSize =
                                    columnResults.getInt("COLUMN_SIZE");

                            Integer length =
                                    columnResults.wasNull()
                                            ? null : columnSize;

                            boolean nullable =
                                    columnResults.getInt("NULLABLE")
                                            == DatabaseMetaData.columnNullable;

                            String defaultValue =
                                    columnResults.getString("COLUMN_DEF");

                            ColumnInfo column =
                                    new ColumnInfo(
                                            columnName,
                                            dataType,
                                            nullable
                                    );

                            column.setLength(length);
                            column.setDefaultValue(defaultValue);

                            columns.add(column);
                        }
                    }

                    tables.add(new TableInfo(tableName, columns));
                }
            }
        }

        return tables;
    }
}
