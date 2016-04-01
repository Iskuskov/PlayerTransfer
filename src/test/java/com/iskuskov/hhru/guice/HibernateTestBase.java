package com.iskuskov.hhru.guice;

import com.iskuskov.hhru.utils.ResourceUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Iskuskov on 24.03.2016.
 */
public class HibernateTestBase extends GuiceTestBase {

    protected static final SessionFactory sessionFactory = getInstance(SessionFactory.class);

    static {
        try {
            createTables();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("failed to create tables", e);
        }
    }

    private static void createTables() throws IOException, URISyntaxException {

        final String sql = ResourceUtils.read("create-tables.sql", ClassLoader.getSystemClassLoader());
        final Session session = sessionFactory.getCurrentSession();
        final Transaction transaction = session.beginTransaction();
        try {
            session.createSQLQuery(sql).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @After
    public void hibernateTestBaseTearDown() throws Exception {
        cleanTables();
    }

    private static void cleanTables() {

        final Set<String> tablesNames = sessionFactory.getAllClassMetadata().values().stream()
                .map(classMetadata -> ((AbstractEntityPersister) classMetadata).getTableName())
                .collect(Collectors.toSet());

        final Session session = sessionFactory.getCurrentSession();
        final Transaction transaction = session.beginTransaction();
        try {
            // too primitive: will not work if tables have FKs
            tablesNames.stream()
                    .forEach(tableName -> session.createSQLQuery("DELETE FROM " + tableName).executeUpdate());
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}
