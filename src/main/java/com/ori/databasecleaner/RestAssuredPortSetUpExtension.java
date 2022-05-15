package com.ori.databasecleaner;

import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.web.server.LocalServerPort;

public class RestAssuredPortSetUpExtension implements BeforeEachCallback {

    @LocalServerPort
    int port;

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        RestAssured.port = port;
    }
}
