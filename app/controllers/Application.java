package controllers;

import play.*;
import play.libs.Mail;
import play.mvc.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import models.*;

@With(Secure.class)
public class Application extends Controller {

    public static void index() throws EmailException {
    	String user = Security.connected();
        render(user);
    }

}