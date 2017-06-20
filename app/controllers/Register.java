package controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import models.Role;
import models.User;
import play.mvc.Controller;

public class Register extends Controller {
	
	public static void show() {
		renderTemplate("Register/show.html");
	}
	
	public static void register(String username, String email, String password) 
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String response = "";
		
		if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
			response = "Please enter values in all fields!";
			renderTemplate("Register/show.html", response);
			return;
		}
		
		if (password.length() > 100) {
			response = "Password to long!";
			renderTemplate("Register/show.html", response);
			return;
		}
		
		byte[] salt = HashFunction.getSalt();
		String hash = HashFunction.hashPassword(password, salt);
		List<Role> roles = (List) Role.find("byName", "business partner").fetch();
		if(!roles.isEmpty()){
			Role role = roles.get(0);
			User user = new User(username, hash, email, salt, role);
			user.save();
			redirect("http://localhost:9000/secure/login", true);
		}
		
		
	}
}
