package com.iskuskov.hhru.players;

import com.google.inject.Inject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Iskuskov on 22.03.2016.
 */
public class PlayerSpringJDBCDAO implements PlayerDAO {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    @Inject
    public PlayerSpringJDBCDAO(final DataSource dataSource) {

        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("players")
                .usingGeneratedKeyColumns("player_id");
    }

    @Override
    public void insert(final Player player) {

        if (player.id() != null) {
            throw new IllegalArgumentException("can not insert " + player + " with already assigned id");
        }

        final Map<String, Object> params = new HashMap<>();
        params.put("player_name", player.playerName());
        params.put("nationality", player.nationality());
        params.put("market_value", player.marketValue());
        params.put("team_id", player.teamId());

        final int playerId = simpleJdbcInsert.executeAndReturnKey(params).intValue();
        player.setId(playerId);
    }

    @Override
    public Optional<Player> get(final int playerId) {

        final String query =
                "SELECT player_id, player_name, nationality, market_value, team_id " +
                "FROM players WHERE player_id = :player_id";

        final Map<String, Object> params = new HashMap<>();
        params.put("player_id", playerId);

        final Player player;
        try {
            player = namedParameterJdbcTemplate.queryForObject(query, params, rowToUser);
        } catch (final EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
        return Optional.of(player);
    }

    @Override
    public Set<Player> getAll() {

        final String query = "SELECT player_id, player_name, nationality, market_value, team_id FROM players";

        return new HashSet<>(jdbcTemplate.query(query, rowToUser));
    }

    @Override
    public void update(final Player player) {

        if (player.id() == null) {
            throw new IllegalArgumentException("can not update " + player + " without id");
        }

        final String query =
                "UPDATE players SET " +
                "player_name = :player_name, nationality = :nationality, " +
                "market_value = :market_value, team_id = :team_id " +
                "WHERE player_id = :player_id";

        final Map<String, Object> params = new HashMap<>();
        params.put("player_name", player.playerName());
        params.put("nationality", player.nationality());
        params.put("market_value", player.marketValue());
        params.put("team_id", player.teamId());
        params.put("player_id", player.id());

        namedParameterJdbcTemplate.update(query, params);
    }

    @Override
    public void delete(final int playerId) {

        final String query = "DELETE FROM players WHERE player_id = :player_id";

        final Map<String, Object> params = new HashMap<>();
        params.put("player_id", playerId);

        namedParameterJdbcTemplate.update(query, params);
    }

    private static final RowMapper<Player> rowToUser = (resultSet, rowNum) ->
            Player.existing(
                    resultSet.getInt("player_id"),
                    resultSet.getString("player_name"),
                    resultSet.getString("nationality"),
                    resultSet.getInt("market_value"),
                    resultSet.getInt("team_id")
            );
}
