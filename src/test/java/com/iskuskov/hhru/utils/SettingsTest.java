package com.iskuskov.hhru.utils;

import com.typesafe.config.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Iskuskov on 24.03.2016.
 */
public class SettingsTest {
    @Test(expected = ConfigException.Missing.class)
    public void shouldThrowConfigExceptionMissingIfDatabaseSectionNotFound() throws Exception {

        final com.typesafe.config.Config config = ConfigFactory.parseResourcesAnySyntax("missing.conf");
        assertFalse(config.hasPath("database"));

        new Settings(config).databaseUrl();
    }

    @Test(expected = ConfigException.Missing.class)
    public void shouldThrowConfigExceptionMissingIfDatabasePasswordNotFound() throws Exception {

        final com.typesafe.config.Config config =
                ConfigFactory.parseResourcesAnySyntax("com/iskuskov/hhru/utils/missingDatabasePassword.conf");
        assertTrue(config.hasPath("database"));

        new Settings(config).databasePassword();
    }

    @Test
    public void shouldReturnSettings() throws Exception {

        final com.typesafe.config.Config config = ConfigFactory.load("com/iskuskov/hhru/utils/application.conf");

        final Settings settings = new Settings(config);

        assertEquals("some url", settings.databaseUrl());
        assertEquals("some user", settings.databaseUser());
        assertEquals("some password", settings.databasePassword());
    }
}