package com.iskuskov.hhru.utils;

import com.iskuskov.hhru.players.Player;
import com.iskuskov.hhru.teams.Team;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.postgresql.ds.PGSimpleDataSource;

import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by Iskuskov on 23.03.2016.
 */
public class Config {

    public static DataSource getDataSource(
            @Named("databaseUrl") final String url,
            @Named("databaseUser") final String user,
            @Named("databasePassword") final String password) throws SQLException {

        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    public static SessionFactory getSessionFactory(
            @Named("databaseUrl") final String url,
            @Named("databaseUser") final String user,
            @Named("databasePassword") final String password
    ) {
        Configuration configuration = new Configuration().addAnnotatedClass(Team.class);
        configuration.setProperty("hibernate.connection.url", url)
                     .setProperty("hibernate.connection.username", user)
                     .setProperty("hibernate.connection.password", password)
                     .setProperty("hibernate.current_session_context_class", "thread");

        return configuration.buildSessionFactory();
        //return new Configuration().addAnnotatedClass(Player.class).buildSessionFactory();
    }

    public static SessionFactory getTestSessionFactory() {
        Configuration configuration = new Configuration().addAnnotatedClass(Team.class);
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                     .setProperty("hibernate.connection.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                     .setProperty("hibernate.current_session_context_class", "thread")
                     .setProperty("hibernate.id.new_generator_mappings", "true");
        return configuration.buildSessionFactory();
    }


}
