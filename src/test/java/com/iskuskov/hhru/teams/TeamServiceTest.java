package com.iskuskov.hhru.teams;

import com.google.common.collect.ImmutableSet;
import com.iskuskov.hhru.guice.HibernateTestBase;
import com.iskuskov.hhru.players.Player;
import com.iskuskov.hhru.players.PlayerService;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Iskuskov on 24.03.2016.
 */
public class TeamServiceTest extends HibernateTestBase {

    final TeamService teamService = getInstance(TeamService.class);
    final PlayerService playerService = getInstance(PlayerService.class);

    @Test
    public void saveShouldInsertTeamInDBAndReturnTeamWithId() throws Exception {

        final Team team1 = new Team("Arsenal", "ENG", 200 * 1000 * 1000);
        final Team team2 = new Team("AS Roma", "ITA", 350 * 1000 * 1000);

        teamService.insert(team1);
        teamService.insert(team2);

        assertEquals("Arsenal", team1.teamName());
        assertEquals("ENG", team1.country());
        assertEquals(team1, teamService.get(team1.id()).get());

        assertEquals("AS Roma", team2.teamName());
        assertEquals("ITA", team2.country());
        assertEquals(team2, teamService.get(team2.id()).get());
    }

    @Test
    public void getShouldReturnTeamById() throws Exception {

        final Team team = new Team("Bayern", "GER", 500 * 1000 * 1000);
        teamService.insert(team);

        final Optional<Team> teamFromDB = teamService.get(team.id());

        assertEquals(team, teamFromDB.get());
    }

    @Test
    public void getShouldReturnEmptyOptionalIfNoTeamWithSuchId() throws Exception {

        final int nonExistentTeamId = 123;

        final Optional<Team> team = teamService.get(nonExistentTeamId);

        assertFalse(team.isPresent());
    }

    @Test
    public void getAllShouldReturnAllTeams() throws Exception {

        final Team team1 = new Team("Arsenal", "ENG", 200 * 1000 * 1000);
        final Team team2 = new Team("AS Roma", "ITA", 350 * 1000 * 1000);
        teamService.insert(team1);
        teamService.insert(team2);

        final Set<Team> teams = teamService.getAll();

        assertEquals(ImmutableSet.of(team1, team2), teams);
    }

    @Test
    public void changeBudgetShouldChangeBudget() throws Exception {

        final Team team = new Team("AC Milan", "ITA", 400 * 1000 * 1000);
        teamService.insert(team);

        final Integer newBudget = 700 * 1000 * 1000;
        teamService.changeBudget(team.id(), 700 * 1000 * 1000);

        final Team teamFromDB = teamService.get(team.id()).get();
        assertEquals(newBudget, teamFromDB.budget());
        assertEquals(team.teamName(), teamFromDB.teamName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void changeBudgetShouldThrowIllegalArgumentExceptionIfNoTeamWithSuchId() throws Exception {

        final int nonExistentTeamId = 123;
        assertFalse(teamService.get(nonExistentTeamId).isPresent());

        teamService.changeBudget(nonExistentTeamId, 700 * 1000 * 1000);
    }

    @Test
    public void deleteShouldDeleteTeamById() throws Exception {

        final Team team1 = new Team("Arsenal", "ENG", 200 * 1000 * 1000);
        final Team team2 = new Team("AS Roma", "ITA", 350 * 1000 * 1000);

        teamService.insert(team1);
        teamService.insert(team2);

        teamService.delete(team1.id());

        assertFalse(teamService.get(team1.id()).isPresent());
        assertTrue(teamService.get(team2.id()).isPresent());
    }

    @Test
    public void transferPlayer() throws Exception {

        final Integer juveBudget = 400 * 1000 * 1000;
        final Integer realBudget = 2000 * 1000 * 1000;
        final Integer zidaneMarketValue = 50 * 1000 * 1000;

        final Team juve = new Team("Juventus", "ITA", juveBudget);
        final Team real = new Team("Real Madrid", "ESP", realBudget);

        teamService.insert(juve);
        teamService.insert(real);

        final Player zidane = Player.create("Zinedine Zidane", "FRA", zidaneMarketValue, juve.id());
        playerService.insert(zidane);

        // Before transfer
        assertEquals(juveBudget, juve.budget());
        assertEquals(realBudget, real.budget());
        assertEquals(zidane.teamId(), juve.id());

        // Transfer
        teamService.contractPlayer(real.id(), zidane.id());

        // After transfer
        final Team juveFromDB = teamService.get(juve.id()).get();
        final Team realFromDB = teamService.get(real.id()).get();
        final Player zidaneFromDB = playerService.get(zidane.id()).get();

        final Integer newJuveBudget = juveBudget + zidaneMarketValue;
        final Integer newRealBudget = realBudget - zidaneMarketValue;

        assertEquals(newJuveBudget, juveFromDB.budget());
        assertEquals(newRealBudget, realFromDB.budget());
        assertEquals(real.id(), zidaneFromDB.teamId());
    }
}