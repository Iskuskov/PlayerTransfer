package com.iskuskov.hhru.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.iskuskov.hhru.utils.Config;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * guice module that declares beans only for testing purposes
 **/
class GuiceTestOnlyModule extends AbstractModule {

    @Override
        protected void configure() {
    }

    @Provides
    @javax.inject.Singleton
    SessionFactory provideEmbeddedSessionFactory() throws SQLException {
        return Config.getTestSessionFactory();
    }

    @Provides
    @Singleton
    static DataSource provideEmbeddedDataSource() {

        final EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("create-tables.sql")
                .build();
        closeEmbeddedDatabaseOnShutdown(embeddedDatabase);
        return embeddedDatabase;
    }

    private static void closeEmbeddedDatabaseOnShutdown(final EmbeddedDatabase embeddedDatabase) {
        Runtime.getRuntime().addShutdownHook(new Thread(embeddedDatabase::shutdown));
    }
}
