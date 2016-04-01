package com.iskuskov.hhru.players;

/**
 * Created by Iskuskov on 24.03.2016.
 */
public class PlayerSpringJDBCDAOTest extends PlayerDAOTestBase {

    private static final PlayerSpringJDBCDAO playerSpringJDBCDAO = getInstance(PlayerSpringJDBCDAO.class);

    @Override
    protected PlayerDAO playerDAO() {
        return playerSpringJDBCDAO;
    }
}
