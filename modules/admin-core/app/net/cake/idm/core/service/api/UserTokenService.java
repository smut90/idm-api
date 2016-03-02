package net.cake.idm.core.service.api;

import org.keycloak.representations.idm.UserRepresentation;

/**
 * Interface for UserTokenService
 *
 * @author  Supun Muthutantri
 * @date    12/02/2016
 *
 */
public interface UserTokenService {

    /**
     * Method to create and persist user token for the provided username
     *
     * @param userName
     * @return
     */
    String generateUserToken(String userName);

    /**
     * The method to retrieve user details for the provided user token
     *
     * @param token
     * @return
     */
    UserRepresentation getUserByToken(String token);
}
