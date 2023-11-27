package com.somerdin.thesaurus.util;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class SqlDataSourceUtil {
    public static DataSource getDataSource() {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setServerName("localhost");
        source.setPortNumber(5432);
        source.setUser("postgres");
        source.setPassword("postgres1");
        source.setDatabaseName("thesaurus");
        return source;
    }
}
