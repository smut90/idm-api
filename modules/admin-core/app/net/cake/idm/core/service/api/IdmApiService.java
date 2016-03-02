package net.cake.idm.core.service.api;

import net.cake.idm.core.exception.IDMException;
import org.keycloak.representations.idm.UserRepresentation;

/**
 * This interface is the contract maintained in order to utilize IDM Rest APIs
 *
 * @author Chamantha De Silva
 * @author Supun Muthutantri
 */
public interface IdmApiService {

    /**
     * This method will update user on Leapset IDM
     *
     * @param userRepresentation
     * @throws IDMException
     */
    void updateUserByUsername(UserRepresentation userRepresentation)
            throws IDMException;


    /**
     * The method to fetch the user by username from idm
     *
     * @param username
     * @throws IDMException
     */
    UserRepresentation getUserByUsername(String username)
            throws IDMException;
}
