/*package controllers;

import models.User;

public class Security extends Secure.Security {

	static boolean authentication(String username, String password) {
		User user = User.find("byUsername", username).first();
		return user != null && user.password.equals(password);
	}
}

*/