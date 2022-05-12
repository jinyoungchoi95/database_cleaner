package com.ori.databasecleaner;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;


@SpringBootTest
@ExtendWith(DatabaseCleanerExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class DatabaseCleanerExtensionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    @DisplayName("table을 세팅하고 데이터를 삽입한다.")
    void setUpTableAndInsertData() {
        jdbcTemplate.execute("DROP TABLE station IF EXISTS");
        jdbcTemplate.execute("DROP TABLE line IF EXISTS");
        jdbcTemplate.execute("DROP TABLE section IF EXISTS");

        jdbcTemplate.execute("CREATE TABLE station (id int auto_increment, name varchar(10));");
        jdbcTemplate.execute("CREATE TABLE line (id int auto_increment);");
        jdbcTemplate.execute("CREATE TABLE section (id int auto_increment);");

        jdbcTemplate.update("insert into station (name) values ('name')");

        boolean isExistsFeild = jdbcTemplate.queryForObject("select exists (select * from station)", Boolean.class);
        assertThat(isExistsFeild).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("truncate 실행으로 테이블 데이터가 존재하지 않는다.")
    void tableIsCleared() {
        boolean isExistsFeild = jdbcTemplate.queryForObject("select exists (select * from station)", Boolean.class);
        assertThat(isExistsFeild).isFalse();
    }
}
