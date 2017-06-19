package controllers;

import models.User;

public class Security extends Secure.Security {


	static boolean authenticate(String username, String password) {
		Application.logIPToFile(request.remoteAddress);
		User user = User.find("byUsername", username).first();
		session.put("role", user.role.name);
		return user != null && user.password.equals(password);
	}
}

