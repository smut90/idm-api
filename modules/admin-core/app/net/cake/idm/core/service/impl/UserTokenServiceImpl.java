package net.cake.idm.core.service.impl;

import com.leapset.auth.core.security.LeapsetAuthenticationException;
import com.leapset.auth.core.token.util.TokenUtil;
import com.leapset.auth.core.util.StatusCode;
import net.cake.idm.core.to.UserTokenTo;
import net.cake.idm.core.dao.UserTokenDao;
import net.cake.idm.core.exception.IDMException;
import net.cake.idm.core.service.api.IdmApiService;
import net.cake.idm.core.service.api.UserTokenService;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static java.text.MessageFormat.format;

/**
 * //Todo: check generateUserToken
 * Implementing provided UserTokenService methods
 *
 * @author : Supun Muthutantri
 * @date : 12/02/2016
 */
public class UserTokenServiceImpl implements UserTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenServiceImpl.class);
    private final static Integer userTokenExpiration = Play.application().configuration().getInt("leapset.idm.user.token.expiration");

    @Inject
    private IdmApiService idmApiService;

    @Inject
    private UserTokenDao userTokenDao;

    /**
     * Returns a token for a valid idm user.
     *
     * @param userName Username.
     * @return token for a existing user
     */
    @Override
    public String generateUserToken(String userName) {

        LOGGER.info("Entered user Token generation...");

        if (StringUtils.isBlank(userName)) {
            String error = "username not provided with the request";
            LOGGER.error(error);
            throw new LeapsetAuthenticationException(StatusCode.INVALID_REQUEST, error);
        }

        UserRepresentation existingUser;
        try {
            existingUser = idmApiService.getUserByUsername(userName);
            if (existingUser == null) {
                LOGGER.error("User does not exist for the provided username {}", userName);
                throw new LeapsetAuthenticationException(StatusCode.USER_NOT_FOUND, "User not found");
            }
        } catch (IDMException e) {
            LOGGER.error("User does not exist for the provided username {}", userName);
            throw new LeapsetAuthenticationException(StatusCode.USER_NOT_FOUND, "User not found");
        }

        String token = TokenUtil.generateRandomAlphaString();
        UserTokenTo userTokenTo = new UserTokenTo(existingUser.getId(),
                existingUser.getUsername(),
                token,
                existingUser.getCreatedTimestamp(),//Todo: Added later
                getExpirationTime());
        Boolean isUpdated = userTokenDao.updateUserTokenInfo(userTokenTo);
        if (!isUpdated) {
            userTokenDao.persistUserTokenInfo(userTokenTo);
        }
        return token;
    }

    @Override
    public UserRepresentation getUserByToken(String token) {

        LOGGER.info("Entered getUserByToken for token {}", token);

        UserRepresentation existingUser;
        if (StringUtils.isBlank(token)) {
            String error = "token not provided with the request";
            LOGGER.error(error);
            throw new LeapsetAuthenticationException(StatusCode.INVALID_REQUEST, error);
        }

        UserTokenTo userTokenInfo = userTokenDao.getUserTokenInfo(token);

        if (userTokenInfo == null) {
            LOGGER.error("User does not exist for the provided token {}", token);
            throw new LeapsetAuthenticationException(StatusCode.INVALID_REQUEST, "User not found");
        }

        validateExpiration(userTokenInfo);

        try {
            String username = userTokenInfo.getUsername();
            //validate user
            existingUser = idmApiService.getUserByUsername(username);

        } catch (IDMException e) {
            LOGGER.error("User does not exist for the provided token {}", token);
            throw new LeapsetAuthenticationException(StatusCode.USER_NOT_FOUND, "User not found");
        }

        return existingUser;
    }

    /**
     * The methos to check the expiration of the token userTokenInfo.getUsername()
     *
     * @param userTokenInfo
     */
    private void validateExpiration(UserTokenTo userTokenInfo) {

        Integer expirationTimeInSec = userTokenInfo.getExpirationTime();
        Long currentTimeInSec = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        LOGGER.info("Verifying expiration time of user token expirationTime (sec) {} currentTime (sec) {}",
                expirationTimeInSec, currentTimeInSec);

        if (currentTimeInSec > expirationTimeInSec) {
            LOGGER.error("User entity token expired for token userTokenID {} expiration Time (sec) {} current Time" +
                    " (sec){}", userTokenInfo.getToken(), expirationTimeInSec, currentTimeInSec);
            throw new LeapsetAuthenticationException(StatusCode.USER_TOKEN_EXPIRED,
                    format("User entity token expired for userToken [{0}]", userTokenInfo));
        }
    }

    private int getExpirationTime() {

        long currentTimeInSec = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        long expirationTime = (currentTimeInSec + userTokenExpiration);
        LOGGER.info("Expiration time for new token {}", expirationTime);
        return (int) expirationTime;
    }
}
