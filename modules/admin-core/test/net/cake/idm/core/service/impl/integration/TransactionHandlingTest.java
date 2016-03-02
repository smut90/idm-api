package net.cake.idm.core.service.impl.integration;

import net.cake.idm.core.to.UserTokenTo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import static org.junit.Assert.fail;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Test cases for Transaction handling
 *
 * @author Supun Muthutantri
 * @date 21/02/2016
 */
public class TransactionHandlingTest {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransactionHandlingTest.class);

    private EntityManagerFactory emfactory;
    private EntityManager entitymanager;
    private EntityTransaction tx;

    @Before
    public void setup() {
        emfactory = Persistence.createEntityManagerFactory("Testing_JPA");
        entitymanager = emfactory.createEntityManager();
        tx = entitymanager.getTransaction();
    }

    @After
    public void close() {
        entitymanager.close();
        emfactory.close();
    }

    /**
     * Persist User failure scenario, commit failure should occur
     */
    @Test
    public void testPersistUserTokenFailure() {
        running(fakeApplication(), () -> {
            try {
                tx.begin();

                UserTokenTo userTokenTo1 = new UserTokenTo();
                userTokenTo1.setUserId("111130e2-2222-47a2-9f6b-32fe2e4917d6");
                userTokenTo1.setUsername("ThisShouldNotPersist@bogus.com");
                userTokenTo1.setToken("ThisShouldNotBeInsideTheDB");
                userTokenTo1.setCreatedTime(1454933294567L);
                userTokenTo1.setUpdatedTime(1454937314440L);
                userTokenTo1.setExpirationTime(1454938214);
                entitymanager.persist(userTokenTo1);

            } finally {
                try {
                    tx.commit();
                    fail("A Commit failure should occur");
                } catch (RollbackException e) {
                    LOGGER.error("Transaction has been rolled back rather than committed", e);
                }
            }
        });
    }

    /**
     * Transaction roll back success scenario
     */
    @Test
    public void testUpdateUserTokenRollback() {
        running(fakeApplication(), () -> {
            try {
                tx.begin();

                UserTokenTo userTokenTo1 = new UserTokenTo();
                userTokenTo1.setUserId("111130e2-2222-47a2-9f6b-32fe2e4917d6");
                userTokenTo1.setUsername("testDBPersist@bogus.com");
                userTokenTo1.setToken("newTokenValueForTesting111");
                entitymanager.merge(userTokenTo1);

                //Checking the updated user token before committing
                Assert.assertEquals("newTokenValueForTesting111", userTokenTo1.getToken());
                tx.setRollbackOnly();
            } finally {
                try {
                    tx.commit();
                    fail("A RollbackException should have been thrown");
                } catch (RollbackException e) {
                    LOGGER.error("Throw an Rolling back exception", e);
                }
            }
        });
    }

    /**
     * Transaction commit success scenario
     */
    @Test
    public void testUpdateUserTokenCommit() {
        running(fakeApplication(), () -> {
            try {
                tx.begin();

                UserTokenTo userTokenTo1 = new UserTokenTo();
                userTokenTo1.setUserId("111130e2-2222-47a2-9f6b-32fe2e4917d6");
                userTokenTo1.setUsername("testDBPersist@bogus.com");
                userTokenTo1.setToken("newTokenValueForTesting111");
                entitymanager.merge(userTokenTo1);

                //Checking the updated user token before committing
                Assert.assertEquals("newTokenValueForTesting111", userTokenTo1.getToken());
            } finally {
                tx.commit();
                LOGGER.info("Committed Successfully");
            }
        });
    }
}
