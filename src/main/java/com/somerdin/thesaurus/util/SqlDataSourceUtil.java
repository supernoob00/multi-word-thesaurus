package com.somerdin.thesaurus.util;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class SqlDataSourceUtil {
    public static final String DB_HOST = "localhost";
    public static final String DB_PORT = "5432";
    public static final String DB_NAME = "thesaurus";

    public static final String DB_USERNAME = "postgres";
    public static final String DB_PASSWORD = "postgres1";

    public static SingleConnectionDataSource getDataSource() {
        SingleConnectionDataSource source = new SingleConnectionDataSource();
        source.setUrl(String.format(
                "jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME));
        source.setUsername("postgres");
        source.setPassword("postgres1");
        source.setAutoCommit(false);
        return source;
    }
}
