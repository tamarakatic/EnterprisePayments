package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class Application extends Controller {
	
//    @Before
//    public static void redirectToHttps() {
//        if (!request.secure && request.headers.get("x-forwarded-proto") != null) {
//            request.secure = request.headers.get("x-forwarded-proto").values.contains("https");
//        }
//
//        if (!request.secure) {
//            redirect(redirectHostHttps() + request.url);
//        }
//    }
//
//    @Util
//    public static String redirectHostHttps() {
//        if (Play.id.equals("dev")) {
//            String httpsPort = (String) Play.configuration.get("https.port");
//            return "https://" + request.host.split(":")[0] + ":" + httpsPort; 
//        } else {
//            if (request.host.endsWith("domain.com")) {
//                return "https://secure.domain.com";
//            } else {
//                return "https://" + request.host;
//            }
//        }
//    }    

    public static void index() {
    	String user = Security.connected();
        render(user);
    }

}