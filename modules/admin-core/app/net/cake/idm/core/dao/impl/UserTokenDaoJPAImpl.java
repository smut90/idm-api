package net.cake.idm.core.dao.impl;

import net.cake.idm.core.to.UserTokenTo;
import net.cake.idm.core.dao.UserTokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.inject.Inject;
import javax.persistence.Query;

/**
 * //Todo:check updateUserTokenInfo
 * <p>
 * Play supported, JPA for transactional management
 *
 * @author Supun Muthutantri
 * @date 17/02/2016
 */
public class UserTokenDaoJPAImpl implements UserTokenDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenDaoJPAImpl.class);
    private final String PARAM_TOKEN = "token";

    @Inject
    private UserTokenTo userTokenTo;

    @Transactional
    @Override
    public void persistUserTokenInfo(UserTokenTo userTokenTo) {
        LOGGER.info("Preparing to persist Idm User token : {}", userTokenTo);
        try {
            JPA.withTransaction(() -> {

                userTokenTo.setUserId(userTokenTo.getUserId());
                userTokenTo.setToken(userTokenTo.getToken());
                userTokenTo.setUsername(userTokenTo.getUsername());
                userTokenTo.setCreatedTime(System.currentTimeMillis());
                userTokenTo.setUpdatedTime(System.currentTimeMillis());
                userTokenTo.setExpirationTime(userTokenTo.getExpirationTime());

                JPA.em().persist(userTokenTo);
            });
        } catch (IllegalArgumentException exception) {
            LOGGER.debug("Cannot persist Idm User token {}", userTokenTo);
        }
    }

    @Transactional
    @Override
    public UserTokenTo getUserTokenInfo(String userToken) {
        LOGGER.info("Preparing to retrieve token information with userToken [{}]", userToken);
        try {
            userTokenTo = JPA.withTransaction(() -> {
                Query query = JPA.em().createQuery("from UserTokenTo  where token=:token");
                query.setParameter(PARAM_TOKEN, userToken);

                userTokenTo = null;
                for (Object o : query.getResultList()) {
                    userTokenTo = (UserTokenTo) o;
                }
                if (userTokenTo == null) {
                    return null;
                }
                return userTokenTo;
            });
        } catch (Throwable throwable) {
            LOGGER.debug("No valid token exists for userToken {}", userToken);
        }
        return userTokenTo;
    }

    @Transactional
    @Override
    public Boolean updateUserTokenInfo(UserTokenTo userTokenTo) {
        LOGGER.info("Preparing to persist Idm User token : {}", userTokenTo);
        try {
            return JPA.withTransaction(() -> {
                userTokenTo.setToken(userTokenTo.getToken());
                userTokenTo.setUserId(userTokenTo.getUserId());
                //Todo:should check added later, to avoid getting null for created time in DB
                userTokenTo.setCreatedTime(userTokenTo.getCreatedTime());
                userTokenTo.setUpdatedTime(System.currentTimeMillis());
                userTokenTo.setExpirationTime(userTokenTo.getExpirationTime());

                JPA.em().merge(userTokenTo);
                return true;
            });
        } catch (Throwable throwable) {
            LOGGER.debug("Cannot update Idm User token {}", userTokenTo);
            throw new IllegalStateException("Error when updating token info", throwable);
        }
    }
}
