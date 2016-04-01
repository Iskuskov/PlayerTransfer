package com.iskuskov.hhru.guice;

import com.google.inject.Injector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.jdbc.JdbcTestUtils;

import static com.google.inject.Guice.createInjector;

/**
 * Created by Iskuskov on 24.03.2016.
 */
public class SpringJDBCTestBase extends GuiceTestBase {

    protected static EmbeddedDatabase database;

    @BeforeClass
    public static void setUpDBTestBaseClass() throws Exception {
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("create-tables.sql")
                .build();
    }

    @AfterClass
    public static void tearDownDBTestBaseClass() throws Exception {
        // TODO: 24.03.2016: failed HibernateTests
        // database.shutdown();
    }

    @After
    public void tearDownGuiceTestBase() throws Exception {
        JdbcTestUtils.deleteFromTables(new JdbcTemplate(database), "players");
        JdbcTestUtils.deleteFromTables(new JdbcTemplate(database), "teams");
    }

}
