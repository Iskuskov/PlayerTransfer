package com.iskuskov.hhru.players;

import com.iskuskov.hhru.guice.SpringJDBCTestBase;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Iskuskov on 24.03.2016.
 */
public abstract class PlayerDAOTestBase extends SpringJDBCTestBase {
    protected abstract PlayerDAO playerDAO();

    @Test
    public void insertShouldInsertNewPlayerInDBAndReturnPlayerWithAssignedId() throws Exception {

        final Player player1 = Player.create("Guilherme Marinato", "BRA", 4500000, null);
        final Player player2 = Player.create("Zinedine Zidane", "FRA", 46000000, null);

        playerDAO().insert(player1);
        playerDAO().insert(player2);

        final Player player1FromDB = playerDAO().get(player1.id()).get();
        assertEquals(player1, player1FromDB);

        final Player player2FromDB = playerDAO().get(player2.id()).get();
        assertEquals(player2, player2FromDB);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertShouldThrowIllegalArgumentExceptionIfPlayerHasId() throws Exception {

        final Player player = Player.existing(1, "player name", "nationality", 0, null);

        playerDAO().insert(player);
    }

    @Test
    public void getShouldReturnPlayer() throws Exception {

        final Player player = Player.create("Zinedine Zidane", "FRA", 46000000, null);
        playerDAO().insert(player);

        final Optional<Player> playerFromDB = playerDAO().get(player.id());

        assertEquals(player, playerFromDB.get());
    }

    @Test
    public void getShouldReturnEmptyOptionalIfNoPlayerWithSuchId() throws Exception {

        final int nonExistentPlayerId = 777;

        final Optional<Player> playerFromDB = playerDAO().get(nonExistentPlayerId);

        assertFalse(playerFromDB.isPresent());
    }

    @Test
    public void getAllShouldReturnAllPlayers() throws Exception {

        assertTrue(playerDAO().getAll().isEmpty());

        final Player player1 = Player.create("Guilherme Marinato", "BRA", 4500000, null);
        final Player player2 = Player.create("Zinedine Zidane", "FRA", 46000000, null);

        playerDAO().insert(player1);
        playerDAO().insert(player2);

        final Set<Player> playersFromDB = playerDAO().getAll();

        assertEquals(new HashSet<>(Arrays.asList(player1, player2)), playersFromDB);
    }

    @Test
    public void updateShouldUpdatePlayer() throws Exception {

        final Player player = Player.create("Zinedine Zidane", "FRA", 46000000, null);
        playerDAO().insert(player);
        player.setPlayerName("Ivan");

        playerDAO().update(player);

        final Player playerFromDB = playerDAO().get(player.id()).get();
        assertEquals(player, playerFromDB);
    }

    @Test
    public void deleteShouldDeletePlayerById() throws Exception {

        final Player player1 = Player.create("Guilherme Marinato", "BRA", 4500000, null);
        final Player player2 = Player.create("Zinedine Zidane", "FRA", 46000000, null);

        playerDAO().insert(player1);
        playerDAO().insert(player2);

        playerDAO().delete(player1.id());

        assertFalse(playerDAO().get(player1.id()).isPresent());
        assertTrue(playerDAO().get(player2.id()).isPresent());
    }
}