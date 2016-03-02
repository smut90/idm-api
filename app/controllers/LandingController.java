package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Once the main play application started, will be redirected to this controller
 * In order to invoke services, should reroute to following mentioned URL's
 *
 * @author Supun Muthutantrige
 * @date 22/02/2016
 */
public class LandingController extends Controller {

    public Result index() {

        return ok(
                "Method\t" + "URL\t" + "Ex\n\n" +
                        "GET:[Generate a token for a username]\t" + "http://<host>:<port>/idm/admin/users/token/{userName}\t" + "Eg: http://localhost:9000/idm/admin/users/token/cheesy@leapset.com\n" +
                        "GET:[Retrieve a user by a token]\t" + "http://<host>:<port>/idm/admin/users/user/{token}\t" + "Eg: http://localhost:9000/idm/admin/users/user/1asFwecdcsdr33dsdas"
        );
    }

}
