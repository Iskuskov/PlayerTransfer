package com.iskuskov.hhru.utils;

import static java.util.Objects.requireNonNull;
import com.typesafe.config.Config;

/**
 * Created by Iskuskov on 23.03.2016.
 */

public class Settings {

    private final Config config;

    public Settings(final Config config) {
        this.config = requireNonNull(config);
    }

    public String databaseUrl() {
        return config.getString("jdbc.url");
    }

    public String databaseUser() {
        return config.getString("jdbc.user");
    }

    public String databasePassword() {
        return config.getString("jdbc.password");
    }
}