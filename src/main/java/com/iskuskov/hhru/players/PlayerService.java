package com.iskuskov.hhru.players;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Created by Iskuskov on 23.03.2016.
 */

public class PlayerService {

    private final SessionFactory sessionFactory;
    private final PlayerDAO playerDAO;

    @Inject
    public PlayerService(final SessionFactory sessionFactory, final PlayerDAO playerDAO) {
        this.sessionFactory = requireNonNull(sessionFactory);
        this.playerDAO = requireNonNull(playerDAO);
    }

    public void insert(final Player player) {
        inTransaction(() -> playerDAO.insert(player));
    }

    public Optional<Player> get(final int playerId) {
        return inTransaction(() -> playerDAO.get(playerId));
    }

    public Set<Player> getAll() {
        return inTransaction(playerDAO::getAll);
    }

    public void update(final Player player) {
        inTransaction(() -> playerDAO.update(player));
    }

    public void delete(final int playerId) {
        inTransaction(() -> playerDAO.delete(playerId));
    }

    public void changeMarketValue(final int playerId, final int newMarketValue) {
        inTransaction(() -> {
            final Optional<Player> optionalPlayer = playerDAO.get(playerId);
            if (!optionalPlayer.isPresent()) {
                throw new IllegalArgumentException("there is no player with id " + playerId);
            }
            optionalPlayer.get().setMarketValue(newMarketValue);
            update(optionalPlayer.get());
        });
    }

    public void changeTeam(final int playerId, final int newTeamId) {
        inTransaction(() -> {
            final Optional<Player> optionalPlayer = playerDAO.get(playerId);
            if (!optionalPlayer.isPresent()) {
                throw new IllegalArgumentException("there is no player with id " + playerId);
            }
            optionalPlayer.get().setTeamId(newTeamId);
            update(optionalPlayer.get());
        });
    }

    private <T> T inTransaction(final Supplier<T> supplier) {
        final Optional<Transaction> transaction = beginTransaction();
        try {
            final T result = supplier.get();
            transaction.ifPresent(Transaction::commit);
            return result;
        } catch (RuntimeException e) {
            transaction.ifPresent(Transaction::rollback);
            throw e;
        }
    }

    private void inTransaction(final Runnable runnable) {
        inTransaction(() -> {
            runnable.run();
            return null;
        });
    }

    private Optional<Transaction> beginTransaction() {
        final Transaction transaction = sessionFactory.getCurrentSession().getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
            return Optional.of(transaction);
        }
        return Optional.empty();
    }
}
