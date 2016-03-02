package net.cake.idm.api.controller.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;

import net.cake.idm.api.controller.UserTokenController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.representations.idm.UserRepresentation;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import net.cake.idm.core.service.api.UserTokenService;
import play.test.FakeApplication;
import play.test.Helpers;

import javax.inject.Inject;

/**
 * Unit tests for available service calls
 *
 * @author Supun Muthutantri
 * @Date 21/02/2016
 */
public class APIServiceCallsTest {

    @Inject
    private UserTokenService userTokenService;

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

    //Should fail due to null user
    @Test
    public void testGetUserByTokenPassingNullValue() {
        F.Promise<Result> result = new UserTokenController().getUserByToken(null);
        assertFalse("Should fail for passing null value: ", result.get(10000L).status() == OK);
    }

    //Should fail due to empty user
    @Test
    public void testGetUserByTokenPassingEmptyValue() {
        F.Promise<Result> result = new UserTokenController().getUserByToken("");
        assertFalse("Should fail for passing empty value: ", result.get(10000L).status() == OK);
    }

    //Success Scenario
    @Test
    public void testGetUserByToken() {
        UserTokenService userTokenServiceMock = mock(UserTokenService.class);

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setUsername("cheesy@leapset.com");
        existingUser.setFirstName("cheesy");
        existingUser.setLastName("Y");
        existingUser.setEmail("cheesy@leapset.com");
        existingUser.setId("cheesy12345678");

        String token = "h71pa2uk2qmvnigqlfjriupaaa";

        when(userTokenServiceMock.getUserByToken(token)).thenReturn(existingUser);

        F.Promise<Result> result = new UserTokenController().getUserByToken(token);
        assertTrue("Should not fail: ", result.get(1000L).status() == OK);
    }

    // Testing Bad Request
    @Test
    public void testBadRequest() {
        String username = "dummy@dummy.com";
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method("GET")
                .uri("/idm/admin/users/token/" + username);

        Result result = route(request);
        assertEquals(404, result.status());
    }

}
