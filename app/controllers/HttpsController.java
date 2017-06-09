package controllers;

import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Util;

public class HttpsController extends Controller {

	@Before
    public static void redirectToHttps() {
        if (!request.secure) {
            redirect(redirectHostHttps() + request.url);
        }
    }

    @Util
    public static String redirectHostHttps() {
        String httpsPort = (String) Play.configuration.get("https.port");
        return "https://" + request.host.split(":")[0] + ":" + httpsPort;
    }    

}
