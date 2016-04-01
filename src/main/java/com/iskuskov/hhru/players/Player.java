/**
 * Created by Iskuskov on 22.03.2016.
 */

package com.iskuskov.hhru.players;

import java.util.Objects;

public class Player {

    private Integer id; // problem: id is null for new player and not null for existing player
                        // seems that NewPlayer and PersistedPlayer are distinct classes
                        // or you can generate id outside database, in order not to rely on database sequence,
                        // which is good for distributed system

    private String playerName;
    private String nationality;
    private Integer marketValue;
    private Integer teamId;

    // factory method to create new player
    // can be constructor, but factory method has name that helps to understand its purpose
    public static Player create(final String playerName, final String nationality,
                                final Integer marketValue, final Integer teamId) {
        return new Player(null, playerName, nationality, marketValue, teamId);
    }

    // factory method to load user from db
    // only PlayerDAO in the same package should use it, that is why it case package private visibility
    // id parameter is int - not Integer - existing user should always have id
    static Player existing(final int id, final String playerName,
                           final String nationality, final Integer marketValue,
                           final Integer teamId) {
        return new Player(id, playerName, nationality, marketValue, teamId);
    }

    // private constructor, only factory methods can use it
    private Player(final Integer id, final String playerName,
                   final String nationality, final Integer marketValue,
                   final Integer teamId) {
        this.id = id;
        this.playerName = playerName;
        this.nationality = nationality;
        this.marketValue = marketValue;
        this.teamId = teamId;
    }

    public Integer id() { return id; }

    // setter is package private - not public - to prevent changing id from outside
    // also id parameter is int, not Integer to prevent setting null
    void setId(final int id) {
        this.id = id;
    }

    public String playerName() { return playerName; }

    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String nationality() { return nationality; }

    public void setNationality(String nationality) { this.nationality = nationality; }

    public Integer marketValue() { return marketValue; }

    public void setMarketValue(Integer marketValue) { this.marketValue = marketValue; }

    public Integer teamId() { return teamId; }

    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public boolean isFreeAgent() {
        return teamId == null;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final Player thatPlayer = (Player) that;
        return Objects.equals(id, thatPlayer.id)
                && Objects.equals(playerName, thatPlayer.playerName)
                && Objects.equals(nationality, thatPlayer.nationality)
                && Objects.equals(marketValue, thatPlayer.marketValue);
                //&& Objects.equals(teamId, thatPlayer.teamId);
    }

    @Override
    public int hashCode() {
        // all new players will have the same hashCode, which might lead to poor Map and Set performance
        // on the other side this hashCode implementation is super fast
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, playerName='%s', country='%s', " +
                "marketValue='%s', freeAgent='%s', teamId=%d}",
                getClass().getSimpleName(), id, playerName,
                nationality, marketValue, isFreeAgent(), teamId);
    }
}
