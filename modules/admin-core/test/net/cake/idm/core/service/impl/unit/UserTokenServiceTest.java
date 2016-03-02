package net.cake.idm.core.service.impl.unit;

import com.leapset.auth.core.security.LeapsetAuthenticationException;
import com.leapset.auth.core.util.StatusCode;
import net.cake.idm.core.dao.UserTokenDao;
import net.cake.idm.core.exception.IDMException;
import net.cake.idm.core.service.api.IdmApiService;
import net.cake.idm.core.service.api.UserTokenService;
import net.cake.idm.core.to.UserTokenTo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.test.FakeApplication;
import play.test.Helpers;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing generateUserToken and getUserByToken
 *
 * @author Supun Muthutantri
 * @date 22/02/2016
 */
public class UserTokenServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenServiceTest.class);
    private FakeApplication app;

    private FakeApplication provideFakeApplication() {
        return Helpers.fakeApplication();
    }

    @Before
    public void startPlay() {
        app = provideFakeApplication();
        Helpers.start(app);
    }

    @After
    public void stopPlay() {
        if (app != null) {
            Helpers.stop(app);
            app = null;
        }
    }

    @Test
    public void testGenerateUserToken() {
        IdmApiService idmApiServiceMock = mock(IdmApiService.class);
        UserTokenService userTokenServiceMock = mock(UserTokenService.class);
        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setUsername("cheesy@leapset.com");
        existingUser.setFirstName("cheesy");
        existingUser.setLastName("Y");
        existingUser.setEmail("cheesy@leapset.com");
        existingUser.setId("cheesy12345678");
        String userName = "cheesy@leapset.com";
        String token = "h71pa2uk2qmvnigqlfjriup5i3";
        try {
            when(idmApiServiceMock.getUserByUsername(userName)).thenReturn(existingUser);
            assertTrue(idmApiServiceMock.getUserByUsername(userName).equals(existingUser));
            verify(idmApiServiceMock).getUserByUsername(userName);
        } catch (IDMException e) {
            LOGGER.error("User does not exist for the provided username {}", userName);
            throw new LeapsetAuthenticationException(StatusCode.USER_NOT_FOUND, "User not found");
        }

        when(userTokenServiceMock.generateUserToken(userName)).thenReturn(token);
        assertTrue(userTokenServiceMock.generateUserToken(userName).equals(token));
        verify(userTokenServiceMock).generateUserToken(userName);
    }

    @Test
    public void testGetUserByToken() {
        IdmApiService idmApiServiceMock = mock(IdmApiService.class);
        UserTokenDao userTokenDaoMock = mock(UserTokenDao.class);
        UserRepresentation existingUser = new UserRepresentation();
        UserTokenTo userTokenInfo = new UserTokenTo("3ee530e2-aa13-47a2-9f6b-32fe2e4917d6",
                "cheesy@leapset.com",
                "h71pa2uk2qmvnigqlfjriup5i3",
                1454901966585L,
                1456810740);
        existingUser.setUsername("cheesy@leapset.com");
        existingUser.setFirstName("cheesy");
        existingUser.setLastName("Y");
        existingUser.setEmail("cheesy@leapset.com");
        existingUser.setId("cheesy12345678");
        String userName = "cheesy@leapset.com";
        String token = "h71pa2uk2qmvnigqlfjriup5i3";

        when(userTokenDaoMock.getUserTokenInfo(token)).thenReturn(userTokenInfo);
        assertTrue(userTokenDaoMock.getUserTokenInfo(token).equals(userTokenInfo));
        verify(userTokenDaoMock).getUserTokenInfo(token);

        try {
            when(idmApiServiceMock.getUserByUsername(userTokenInfo.getUsername())).thenReturn(existingUser);
            assertTrue(idmApiServiceMock.getUserByUsername(userTokenInfo.getUsername()).equals(existingUser));
            verify(idmApiServiceMock).getUserByUsername(userTokenInfo.getUsername());
        } catch (IDMException e) {
            LOGGER.error("User does not exist for the provided username {}", userName);
            throw new LeapsetAuthenticationException(StatusCode.USER_NOT_FOUND, "User not found");
        }
    }

}
