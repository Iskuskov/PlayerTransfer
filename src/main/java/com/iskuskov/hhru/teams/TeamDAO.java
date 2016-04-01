package com.iskuskov.hhru.teams;

import com.iskuskov.hhru.players.Player;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Iskuskov on 22.03.2016.
 */

public interface TeamDAO {

    void insert(Team team);

    Optional<Team> get(int teamId);

    Set<Team> getAll();

    void update(Team team);

    void delete(int teamId);

    static void play(final TeamDAO teamDAO) {

        final Team team = new Team("AS Roma", "ITA", 350000000);
        teamDAO.insert(team);
        System.out.println("persisted " + team);

        team.setBudget(750000000);
        teamDAO.update(team);
        System.out.println("updated team to " + team);

        teamDAO.delete(team.id());
        System.out.println("deleted " + team);

        final Optional<Team> absentTeam = teamDAO.get(team.id());
        System.out.println("tried to get team by " + team.id() + " but got " + absentTeam);
    }
}
