package com.iskuskov.hhru.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.iskuskov.hhru.players.Player;
import com.iskuskov.hhru.players.PlayerDAO;
import com.iskuskov.hhru.players.PlayerSpringJDBCDAO;
import com.iskuskov.hhru.teams.TeamDAO;
import com.iskuskov.hhru.teams.TeamHibernateDAO;
import com.iskuskov.hhru.utils.Config;
import com.iskuskov.hhru.utils.Settings;
import com.typesafe.config.ConfigFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.SQLException;

import static com.google.inject.name.Names.named;

/**
 * guice module that declares beans common for production and testing purposes
 **/
class GuiceCommonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PlayerDAO.class).to(PlayerSpringJDBCDAO.class).in(com.google.inject.Singleton.class);
        bind(TeamDAO.class).to(TeamHibernateDAO.class).in(com.google.inject.Singleton.class);
    }
}
