package com.iskuskov.hhru.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.iskuskov.hhru.players.Player;
import com.iskuskov.hhru.players.PlayerService;
import com.iskuskov.hhru.teams.Team;
import com.iskuskov.hhru.teams.TeamService;

import java.sql.SQLException;

public final class GuiceMain {

    // Million
    public static final int M = 1000000;

    public static void main(final String... args) throws SQLException, InterruptedException {
        // create graph of beans
        final Injector injector = createInjector();

        // get bean from the graph
        //final PlayerDAO playerDAO = injector.getInstance(PlayerDAO.class);
        //final TeamDAO teamDAO = injector.getInstance(TeamDAO.class);
        final TeamService teamService = injector.getInstance(TeamService.class);
        final PlayerService playerService = injector.getInstance(PlayerService.class);

        // use it
        //PlayerDAO.play(playerDAO);
        //TeamDAO.play(teamDAO);

        // transfer
        final Team juve = new Team("Juventus", "ITA", 400 * M);
        teamService.insert(juve);

        final Team real = new Team("Real Madrid", "ESP", 2000 * M);
        teamService.insert(real);

        final Player zidane = Player.create("Zinedine Zidane", "FRA", 50 * M, juve.id());
        playerService.insert(zidane);

        System.out.println("Before transfer:\n" + juve + "\n" + real + "\n" + zidane + "\n\n");
        teamService.contractPlayer(real.id(), zidane.id());

        final Team juveFromDB = teamService.get(juve.id()).get();
        final Team realFromDB = teamService.get(real.id()).get();
        final Player zidaneFromDB = playerService.get(zidane.id()).get();
        System.out.println("After transfer:\n" + juveFromDB + "\n" +
                realFromDB + "\n" + zidaneFromDB + "\n\n");

        // close
        teamService.closeSessionFactory();
    }

    // public for benchmarks
    public static Injector createInjector() {
        return Guice.createInjector(new GuiceCommonModule(), new GuiceProdOnlyModule());
    }

    private GuiceMain() {
    }
}
