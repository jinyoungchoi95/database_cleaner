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
        jdbcTemplate.execute("DROP TABLE section IF EXISTS");
        jdbcTemplate.execute("DROP TABLE station IF EXISTS");
        jdbcTemplate.execute("DROP TABLE line IF EXISTS");

        jdbcTemplate.execute("CREATE TABLE STATION\n"
                + "(\n"
                + "    id   bigint auto_increment not null,\n"
                + "    name varchar(255)          not null unique,\n"
                + "    primary key (id)\n"
                + ");");
        jdbcTemplate.execute("CREATE TABLE LINE\n"
                + "(\n"
                + "    id    bigint auto_increment not null,\n"
                + "    name  varchar(255)          not null unique,\n"
                + "    color varchar(20)           not null,\n"
                + "    primary key (id)\n"
                + ");");
        jdbcTemplate.execute("create table SECTION\n"
                + "(\n"
                + "    id              bigint auto_increment not null,\n"
                + "    up_station_id   bigint                not null,\n"
                + "    primary key (id),\n"
                + "    foreign key (up_station_id) references station (id)"
                + ");");

        jdbcTemplate.update("insert into station (name) values ('name')");
        jdbcTemplate.update("insert into section (up_station_id) values (1)");

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
