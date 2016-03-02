package net.cake.idm.core.dao;

import net.cake.idm.core.to.UserTokenTo;

/**
 * An Entity class has to be mapped to support JPA ORM based implementations,
 * To use different implementation, relevant UserTokenTo type should be imported
 *
 * To support non ORM based implementations, UserTokenTo should be changed from,
 * net.cake.idm.core.to.entity.UserTokenTo to net.cake.idm.core.to.UserTokenTo
 *
 * @author  Supun Muthutantri
 * @date    12/02/2016
 *
 */
public interface UserTokenDao {

    /**
     * Persists idm token data for the provided User ID.
     * @param userTokenTo
     */
    void persistUserTokenInfo(UserTokenTo userTokenTo);

    /**
     * Retrieves the user Id bound to the provided token
     * @param userToken
     * @return
     */
    UserTokenTo getUserTokenInfo(String userToken);

    /**
     * Update user token for provided user id
     * @param userTokenTo
     * @return
     */
    Boolean updateUserTokenInfo(UserTokenTo userTokenTo);
}
