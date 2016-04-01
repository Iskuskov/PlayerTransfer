package com.iskuskov.hhru.players;

/**
 * Created by Iskuskov on 22.03.2016.
 */

import java.util.Optional;
import java.util.Set;

public interface PlayerDAO {

    void insert(Player player);

    Optional<Player> get(int playerId);

    Set<Player> getAll();

    void update(Player player);

    void delete(int playerId);

    static void play(final PlayerDAO playerDAO) {

        final Player player = Player.create("Guilherme Marinato", "BRA", 4500000, null);
        playerDAO.insert(player);
        System.out.println("persisted " + player);

        player.setNationality("RUS");
        playerDAO.update(player);
        System.out.println("updated player to " + player);

        playerDAO.delete(player.id());
        System.out.println("deleted " + player);

        final Optional<Player> absentPlayer = playerDAO.get(player.id());
        System.out.println("tried to get player by " + player.id() + " but got " + absentPlayer);
    }
}
