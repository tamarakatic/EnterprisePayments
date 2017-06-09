package controllers;

import play.mvc.Controller;
import play.mvc.With;

@With({ HttpsController.class, Secure.class })
public class Application extends Controller {

	
	public static void index() {
		String user = Security.connected();
		render(user);
	}

}