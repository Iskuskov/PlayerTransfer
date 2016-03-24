/**
 * Created by Iskuskov on 22.03.2016.
 */

package com.iskuskov.hhru.teams;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Integer id; // problem: id is null for new team and not null for existing team
                        // seems that NewTeam and PersistedTeam are distinct classes

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "country")
    private String country;

    @Column(name = "budget")
    private Integer budget;

    public Team(final String teamName, final String country, final Integer budget) {
        this.teamName = teamName;
        this.country = country;
        this.budget = budget;
    }

    /** for Hibernate only */
    @Deprecated
    Team() {}  // problem: somebody can use this constructor and create inconsistent instance

    public Integer id() { return id; }

    // no setId, Hibernate uses reflection to set field

    public String teamName() { return teamName; }

    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String country() { return country; }

    public void setCountry(String country) { this.country = country; }

    public Integer budget() { return budget; }

    public void setBudget(Integer budget) { this.budget = budget; }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final Team thatTeam = (Team) that;
        return Objects.equals(id, thatTeam.id)
                && Objects.equals(teamName, thatTeam.teamName)
                && Objects.equals(country, thatTeam.country)
                && Objects.equals(budget, thatTeam.budget);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (teamName != null ? teamName.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + budget.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, teamName='%s', country='%s', budget='%s'}",
                getClass().getSimpleName(), id, teamName, country, budget);
    }
}
