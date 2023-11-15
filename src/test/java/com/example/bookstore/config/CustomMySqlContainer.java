package com.example.bookstore.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomMySqlContainer extends MySQLContainer<CustomMySqlContainer> {
    private static final String DB_IMAGE = "mysql:8";

    private static CustomMySqlContainer mySqlContainer;

    private CustomMySqlContainer() {
        super(DB_IMAGE);
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", this.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", this.getUsername());
        System.setProperty("TEST_DB_PASSWORD", this.getPassword());
    }

    @Override
    public void stop() {
        super.stop();
    }

    public static synchronized CustomMySqlContainer getInstance() {
        if (mySqlContainer == null) {
            mySqlContainer = new CustomMySqlContainer();
        }
        return mySqlContainer;
    }
}
