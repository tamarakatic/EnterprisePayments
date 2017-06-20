package controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import models.PasswordChangeRequest;
import models.User;
import play.libs.Mail;
import play.mvc.Controller;

public class ChangePassword extends Controller {
	
	public static void show() {
		renderTemplate("ChangePassword/show.html");
	}
	
	public static String sendEmail(String username) throws EmailException {
		PasswordChangeRequest pcr = new PasswordChangeRequest(username);
		pcr.save();
		
		SimpleEmail email = new SimpleEmail();
    	email.setFrom("isarestaurant2017@gmail.com");
    	email.setAuthentication("isarestaurant2017@gmail.com", "paramore");
    	email.addTo("markozuzic@nordnet.rs");
    	email.setSubject("Password change");
    	email.setMsg(pcr.token);
    	Mail.send(email);
    	return "OK";
	}
	
	public static void update(String token, String password) {
		PasswordChangeRequest pcr =  (PasswordChangeRequest) PasswordChangeRequest.find("token", token).fetch().get(0);
		if (!pcr.isUsed) {
			if (System.currentTimeMillis() - pcr.time < 3600000) {
				User user = (User) User.find("username", pcr.username).fetch().get(0);
				try {
					user.password = HashFunction.hashPassword(password, user.salt);
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					e.printStackTrace();
				}
				user.save();
				pcr.isUsed = true;
				pcr.save();
				redirect("http://localhost:9000/secure/login", true);
			}
			else {
				pcr.isUsed = true;
				pcr.save();
			}
		}
	}
}
