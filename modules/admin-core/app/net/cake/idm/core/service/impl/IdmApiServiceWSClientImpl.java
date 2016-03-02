package net.cake.idm.core.service.impl;

import com.leapset.auth.core.security.LeapsetAuthenticationException;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.AccessTokenResponse;
import com.leapset.auth.core.util.IDMTokenContainer;
import net.cake.idm.core.service.api.IdmApiService;
import net.cake.idm.core.exception.IDMException;
import com.leapset.auth.core.util.AuthConstant;

import static java.text.MessageFormat.format;

import com.leapset.auth.core.util.StatusCode;
import com.leapset.beans.util.JsonConverter;
import org.apache.commons.lang.StringUtils;
import play.libs.ws.WSAuthScheme;
import org.slf4j.LoggerFactory;
import play.libs.ws.WSResponse;
import play.libs.ws.WSRequest;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.HashMap;

import org.slf4j.Logger;

import java.util.Map;

import play.Play;

/**
 * Web service client to invoke IDM services.
 *
 * @author Supun Muthutantrige
 * @date 27/02/2016
 */
public class IdmApiServiceWSClientImpl implements IdmApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdmApiServiceWSClientImpl.class);

    private String url = Play.application().configuration().getString("idm.secure.elb.node");
    private String username = Play.application().configuration().getString("leapset.idm.username");
    private String password = Play.application().configuration().getString("leapset.idm.password");
    private String clientId = Play.application().configuration().getString("leapset.idm.client.id");
    private String clientSecret = Play.application().configuration().getString("leapset.idm.client.secret");
    private String userListEndpoint = Play.application().configuration().getString("leapset.idm.keycloak.user.list.endpoint");
    private String tokenAccessPath = Play.application().configuration().getString("leapset.idm.keycloak.access.point");
    private static final String USERNAME_PASSWORD_PAYLOAD = "username={0}&password={1}";
    private String payload = format(USERNAME_PASSWORD_PAYLOAD, username, password);

    private static final String BEARER_HEADER_VALUE = "Bearer {0}";
    private static final String RETRIEVING_USER = "Retrieving user: {0}";
    private static final String ACCESSING_TOKEN_STORE_FOR_STORED_ACCESS_TOKENS = "Accessing token store for stored access tokens {}";
    private static final String USER_VERIFICATION_FAILED_DUE_TO_KEYCLOAK_ACCESS_TOKEN_SERVICE_FAILURE =
            "User verification failed due to keycloak access token service failure";
    private static final String ERROR_IN_EXECUTE_AND_VALIDATE_REQUEST = "Error in execute and validate request :: ";
    private final String ACCEPT = "Accept";
    private final String CONTENT_TYPE = "content-type";
    private final String APPLICATION_JSON = "application/json";
    private final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

    @Inject
    private IDMTokenContainer tokenStore;

    @Inject
    private AccessTokenResponse accessToken;

    @Inject
    WSClient ws;

    private Map<String, String> headerMap;


    @Override
    public void updateUserByUsername(UserRepresentation userRepresentation) throws IDMException {
        //Todo: This methods needs to be implemented once required
    }

    @Override
    public UserRepresentation getUserByUsername(String username) throws IDMException {

        LOGGER.info(format(RETRIEVING_USER, username));

        if (StringUtils.isBlank(username)) {
            LOGGER.error("Username is null or empty");
            throw new LeapsetAuthenticationException(StatusCode.INVALID_REQUEST, "Username is null or empty");
        }

        headerMap = new HashMap<>();
        addIdmAccessTokenToHeaderMap(headerMap);
        String userListEndpointCont = userListEndpoint + "?username={0}";
        WSResponse userDetailResponse = executeAndValidateRequest(format(userListEndpointCont, username));

        UserRepresentation[] users = JsonConverter.getObjectFromJson(userDetailResponse.asJson().toString(), UserRepresentation[].class);
        UserRepresentation user = getUser(username, users);

        return user;
    }

    private void addIdmAccessTokenToHeaderMap(Map<String, String> headerMap) {
        headerMap.put(AuthConstant.AUTHORIZATION_KEY, format(BEARER_HEADER_VALUE, getAccessToken().getToken()));
    }

    private AccessTokenResponse getAccessToken() {
        LOGGER.info(ACCESSING_TOKEN_STORE_FOR_STORED_ACCESS_TOKENS, tokenStore);

        accessToken = tokenStore.getAccessToken();
        if (accessToken != null) {
            return accessToken;
        }

        WSRequest request = ws.url(url + "/auth/" + tokenAccessPath)
                .setHeader(ACCEPT, APPLICATION_JSON)
                .setHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED)
                .setAuth(clientId, clientSecret, WSAuthScheme.BASIC);

        WSResponse tokenResponse;
        try {
            tokenResponse = request.post(payload).get(10000L);
        } catch (Exception e) {
            LOGGER.error(USER_VERIFICATION_FAILED_DUE_TO_KEYCLOAK_ACCESS_TOKEN_SERVICE_FAILURE);
            throw new LeapsetAuthenticationException(USER_VERIFICATION_FAILED_DUE_TO_KEYCLOAK_ACCESS_TOKEN_SERVICE_FAILURE, "Couldn't retrieve access token");
        }

        AccessTokenResponse accessToken = JsonConverter.getObjectFromJson(tokenResponse.asJson().toString(), AccessTokenResponse.class);
        tokenStore.setAccessToken(accessToken);

        return accessToken;
    }

    private WSResponse executeAndValidateRequest(String endpoint) {
        LOGGER.info("Entered executeAndValidateRequest for method {} for endpoint {}");

        WSRequest request = ws.url(url + "/auth/" + endpoint)
                .setHeader(ACCEPT, APPLICATION_JSON)
                .setHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED)
                .setHeader(AuthConstant.AUTHORIZATION_KEY, format(BEARER_HEADER_VALUE, getAccessToken().getToken()))
                .setAuth(clientId, clientSecret, WSAuthScheme.BASIC);

        WSResponse response;
        try {
            response = request.get().get(10000L);
        } catch (Exception e) {
            LOGGER.error(USER_VERIFICATION_FAILED_DUE_TO_KEYCLOAK_ACCESS_TOKEN_SERVICE_FAILURE);
            throw new LeapsetAuthenticationException(USER_VERIFICATION_FAILED_DUE_TO_KEYCLOAK_ACCESS_TOKEN_SERVICE_FAILURE, "Couldn't retrieve access token");
        }

        if (response == null) {
            LOGGER.error(ERROR_IN_EXECUTE_AND_VALIDATE_REQUEST + "Error in retrieving the user by user name");
            throw new LeapsetAuthenticationException(StatusCode.ERROR_USER_REGISTRATION, "Error in retrieving the user by user name");
        }

        return response;
    }

    /**
     * The method to retrieve user details
     *
     * @param username
     * @param users
     * @return
     */
    private UserRepresentation getUser(String username, UserRepresentation[] users) {

        UserRepresentation user = null;
        if (users == null || users.length <= 0) {
            LOGGER.error("User does not exist for the provided username {}", username);
            throw new LeapsetAuthenticationException(StatusCode.USER_NOT_FOUND, "User does not exist for the provided username");
        }
        if (users.length == 1) {
            user = users[0];
            LOGGER.info("One user returned in the list. Returning user {}", user);
        } else {
            for (int i = 0; i < users.length; i++) {
                UserRepresentation retrievedUser = users[i];
                if (retrievedUser != null && username.equals(retrievedUser.getUsername())) {
                    user = retrievedUser;
                    break;
                }
            }
        }
        if (user == null) {
            LOGGER.error("User does nt exist for the provided username {}", username);
            throw new LeapsetAuthenticationException(StatusCode.USER_NOT_FOUND, "User does not exist for the provided username");
        }
        return user;
    }
}
