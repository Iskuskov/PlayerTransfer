package com.iskuskov.hhru.teams;

import com.google.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Created by Iskuskov on 22.03.2016.
 */
public class TeamHibernateDAO implements TeamDAO {

    private final SessionFactory sessionFactory;

    @Inject
    public TeamHibernateDAO(final SessionFactory sessionFactory) {
        this.sessionFactory = requireNonNull(sessionFactory);
    }

    public void insert(final Team team) {

        // session.save inserts new team with different id even if user already has id
        // this is confusing, we'd better throw exception, unfortunately at runtime only
        if (team.id() != null) {
            throw new IllegalArgumentException("can not save " + team + " with assigned id");
        }
        session().save(team); // see also saveOrUpdate and persist
    }

    public Optional<Team> get(final int teamId) {
        final Team team = (Team) session().get(Team.class, teamId);
        // or session.load, load throws exception if entity with such id not found
        return Optional.ofNullable(team);
    }

    public Set<Team> getAll() {
        final Criteria criteria = session().createCriteria(Team.class); // Criteria query
        // or session().createQuery("FROM Team"); // HQL query
        @SuppressWarnings("unchecked")
        final List<Team> teams = criteria.list();
        return new HashSet<>(teams);
    }

    public void update(final Team team) {
        session().update(team);
        // session.update throws exception if current session already has Team with same id
        // session.merge does not throw exception
    }

    public void delete(final int teamId) {
        session().createQuery("DELETE Team WHERE id = :id") // HQL
                .setInteger("id", teamId)
                .executeUpdate();
        // see also session().delete(team);
        // but first you will need to get this team from DB
        // also be aware that hibernate silently ignores the fact that team may not have id, this most likely an error
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
        // or sessionFactory.openSession(), but do not forget to close it
        // try-with-resource won't work because Session does not implement Autocloseable
    }
}
