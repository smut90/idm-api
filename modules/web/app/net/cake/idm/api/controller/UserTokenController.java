package net.cake.idm.api.controller;

import com.leapset.auth.core.security.LeapsetAuthenticationException;
import com.leapset.auth.core.util.StatusCode;
import net.cake.idm.core.service.api.UserTokenService;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.F;
import play.mvc.Result;
import play.mvc.Controller;

import javax.inject.Inject;

/**
 * The controller layer implementation for token related logic
 * <p>
 * getUserByToken(String token):       Retrieving a user based on a token value
 * generateUserToken(String userName): Generating a token for a valid user
 *
 * @author Supun Muthutantri
 * @date 18/02/2016
 */
public class UserTokenController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenController.class);

    @Inject
    private UserTokenService userTokenService;

    /**
     * Retrieving a valid user based on a token value
     *
     * @param token
     * @return User
     */
    public F.Promise<Result> getUserByToken(String token) {
        return F.Promise.promise(() -> {
            if (token == null || token.isEmpty()) {
                return badRequest("Provide a valid Token");
            } else {
                try {
                    UserRepresentation userInfo = userTokenService.getUserByToken(token);
                    return ok("Username for the requested token: " +
                            userInfo.getUsername() + " | " +
                            userInfo.getFirstName() + " | " +
                            userInfo.getLastName());
                } catch (Exception e) {
                    LOGGER.error("User does not exist for the provided token {}", token);
                    throw new LeapsetAuthenticationException(StatusCode.USER_NOT_FOUND, "User not found");
                }
            }
        });
    }

    /**
     * Generating a token for a valid user
     *
     * @param userName
     * @return token
     */
    public F.Promise<Result> generateUserToken(String userName) {
        return F.Promise.promise(() -> {
            if (userName == null || userName.isEmpty()) {
                return badRequest("Provide a valid username");
            } else {
                try {
                    String token = userTokenService.generateUserToken(userName);
                    return ok("User token for request [" + userName + "]: " + token);
                } catch (Exception e) {
                    LOGGER.error("Token cannot be generated for the provided username {}", userName);
                    throw new LeapsetAuthenticationException(StatusCode.USER_NOT_FOUND, "User not found");
                }
            }
        });
    }
}
