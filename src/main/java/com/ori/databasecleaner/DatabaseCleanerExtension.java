package com.ori.databasecleaner;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseCleanerExtension implements AfterEachCallback {

    @Override
    public void afterEach(final ExtensionContext context) {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        try {
            JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
            DatabaseMetaData metaData = Objects.requireNonNull(jdbcTemplate.getDataSource())
                    .getConnection()
                    .getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = metaData.getTables(null, null, "%", types);

            executeResetTableQuery(jdbcTemplate, rs);
        } catch (Exception exception) {
            throw new RuntimeException();
        }
    }

    private void executeResetTableQuery(final JdbcTemplate jdbcTemplate, final ResultSet rs) throws SQLException {
        Statement statement =  jdbcTemplate.getDataSource()
                .getConnection()
                .createStatement();
        statement.addBatch("SET REFERENTIAL_INTEGRITY FALSE");

        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            System.out.println(tableName);
            statement.addBatch(createTruncateTableQuery(tableName));
            statement.addBatch(createResetAutoIncrementQuery(tableName));
        }
        statement.addBatch("SET REFERENTIAL_INTEGRITY TRUE");
        statement.executeBatch();
    }

    private String createTruncateTableQuery(final String tableName) {
        return "TRUNCATE TABLE " + tableName;
    }

    private String createResetAutoIncrementQuery(final String tableName) {
        return "ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1";
    }
}
