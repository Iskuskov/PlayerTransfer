package com.iskuskov.hhru.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.iskuskov.hhru.players.PlayerDAO;
import com.iskuskov.hhru.players.PlayerSpringJDBCDAO;
import com.iskuskov.hhru.teams.TeamDAO;
import com.iskuskov.hhru.teams.TeamHibernateDAO;
import com.iskuskov.hhru.utils.Config;
//import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.hibernate.SessionFactory;
import org.postgresql.ds.PGSimpleDataSource;
import com.iskuskov.hhru.utils.Settings;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.SQLException;

import static com.google.inject.name.Names.named;

/**
 * guice module that declare beans only for production (not testing) purposes
 *
 * */
class GuiceProdOnlyModule extends AbstractModule {

    @Override
    protected void configure() {
        final com.typesafe.config.Config config = ConfigFactory.load();
        final Settings settings = new Settings(config);

        bind(String.class).annotatedWith(named("databaseUrl")).toInstance(settings.databaseUrl());
        bind(String.class).annotatedWith(named("databaseUser")).toInstance(settings.databaseUser());
        bind(String.class).annotatedWith(named("databasePassword")).toInstance(settings.databasePassword());
        // generally: bind(Abstract.class).annotatedWith(Names.named("foo")).to(FooImplementation.class);
        // that means: give this instance to those beans that demands not any String, but more specific one: annotated with "foo"
    }

    @Provides
    @Singleton
    DataSource provideDataSource(
            @Named("databaseUrl") final String url,
            @Named("databaseUser") final String user,
            @Named("databasePassword") final String password) throws SQLException {
        return Config.getDataSource(url, user, password);
    }

    @Provides
    @Singleton
    SessionFactory provideSessionFactory(
            @javax.inject.Named("databaseUrl") final String url,
            @javax.inject.Named("databaseUser") final String user,
            @javax.inject.Named("databasePassword") final String password) throws SQLException {
        return Config.getSessionFactory(url, user, password);
    }
}
