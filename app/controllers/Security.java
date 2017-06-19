package controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import models.User;

public class Security extends Secure.Security {
	
	public static void show() {
		render("Secure/login.html");
	}

	static boolean authenticate(String username, String password) {
		User user = User.find("byUsername", username).first();
		try {			
			String hash = HashFunction.hashPassword(password, user.salt);
			return user != null && user.password.equals(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return false;
	}
}

