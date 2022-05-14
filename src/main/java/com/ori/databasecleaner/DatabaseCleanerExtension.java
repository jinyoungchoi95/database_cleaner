package com.ori.databasecleaner;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

public class DatabaseCleanerExtension implements AfterEachCallback {

    @Override
    @Transactional
    public void afterEach(final ExtensionContext context) {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        try {
            System.out.println(1);
            JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
            System.out.println(2);
            DatabaseMetaData metaData = Objects.requireNonNull(jdbcTemplate.getDataSource())
                    .getConnection()
                    .getMetaData();
            System.out.println(3);
            String[] types = {"TABLE"};
            ResultSet rs = metaData.getTables(null, null, "%", types);

            System.out.println(4);
            executeResetTableQuery(jdbcTemplate, rs);
            System.out.println(5);
        } catch (Exception exception) {
            System.out.println(6);
            System.out.println(exception.getMessage());
            System.out.println(7);
            throw new RuntimeException();
        }
    }

    private void executeResetTableQuery(final JdbcTemplate jdbcTemplate, final ResultSet rs) throws SQLException {
        System.out.println(1);
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        System.out.println(2);
        while (rs.next()) {
            System.out.println(11);
            String tableName = rs.getString("TABLE_NAME");
            jdbcTemplate.execute(createTruncateTableQuery(tableName));
            System.out.println(22);
            jdbcTemplate.execute(createResetAutoIncrementQuery(tableName));
            System.out.println(33);
        }
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        System.out.println(4);
    }

    private String createTruncateTableQuery(final String tableName) {
        return "TRUNCATE TABLE " + tableName;
    }

    private String createResetAutoIncrementQuery(final String tableName) {
        return "ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1";
    }
}
