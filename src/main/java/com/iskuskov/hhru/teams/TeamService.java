package com.iskuskov.hhru.teams;

import com.google.inject.Inject;
import com.iskuskov.hhru.players.Player;
import com.iskuskov.hhru.players.PlayerService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Created by Iskuskov on 22.03.2016.
 */
public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final SessionFactory sessionFactory;
    private final TeamDAO teamDAO;
    private final PlayerService playerService;

    @Inject
    public TeamService(final SessionFactory sessionFactory, final TeamDAO teamDAO,
                       final PlayerService playerService) {
        this.sessionFactory = requireNonNull(sessionFactory);
        this.teamDAO = requireNonNull(teamDAO);
        this.playerService = requireNonNull(playerService);
    }

    public void insert(final Team team) {
        inTransaction(() -> teamDAO.insert(team));
    }

    public Optional<Team> get(final int teamId) {
        return inTransaction(() -> teamDAO.get(teamId));
    }

    public Set<Team> getAll() {
        return inTransaction(teamDAO::getAll);
    }

    public void update(final Team team) {
        inTransaction(() -> teamDAO.update(team));
    }

    public void delete(final int teamId) {
        inTransaction(() -> teamDAO.delete(teamId));
    }

    public void changeBudget(final int teamId, final int newBudget) {
        log.info("Change Budget team {}", teamId);
        inTransaction(() -> {
            final Optional<Team> optionalTeam = teamDAO.get(teamId);
            if (!optionalTeam.isPresent()) {
                throw new IllegalArgumentException("there is no team with id " + teamId);
            }
            optionalTeam.get().setBudget(newBudget);
        });
    }

    public void contractPlayer(final int teamId, final int playerId) throws SQLException {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            try {
                // TODO: 23.03.2016: 1. Check if Budget > transferFee

                // 1. Player for transfer
                final Optional<Player> optionalTransferPlayer = playerService.get(playerId);
                if (!optionalTransferPlayer.isPresent()) {
                    throw new IllegalArgumentException("there is no transfer player with id " + playerId);
                }
                final Player transferPlayer = optionalTransferPlayer.get();
                final Integer transferFee = transferPlayer.marketValue();

                // 2. Team (transfer to)
                final Optional<Team> optionalTeamTo = get(teamId);
                if (!optionalTeamTo.isPresent()) {
                    throw new IllegalArgumentException("there is no team (transfer to) with id " + teamId);
                }
                final Team teamTo = optionalTeamTo.get();

                // 3. Игрок уже связан контрактом
                if (!transferPlayer.isFreeAgent()) {

                    // 3.1. Team (transfer from)
                    final Integer teamFromId = transferPlayer.teamId();
                    final Optional<Team> optionalTeamFrom = get(teamFromId);
                    if (!optionalTeamFrom.isPresent()) {
                        throw new IllegalArgumentException("there is no team (transfer from) with id " + teamFromId);
                    }
                    final Team teamFrom = optionalTeamFrom.get();

                    // 3.2. Компенсация
                    changeBudget(teamTo.id(), teamTo.budget() - transferFee);
                    changeBudget(teamFrom.id(), teamFrom.budget() + transferFee);
                }

                // 4. Контракт заключен
                playerService.changeTeam(transferPlayer.id(), teamTo.id());

                log.info("Transfer: player {}, from {}, fee {}",
                        transferPlayer, teamTo, transferFee);

                tx.commit();
            } catch (RuntimeException e) {
                tx.rollback();
                sessionFactory.close();
                throw e;
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
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

    public void closeSessionFactory() {
        sessionFactory.close();
        log.info("SessionFactory closed");
    }
}
