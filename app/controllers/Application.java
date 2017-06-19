package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class Application extends Controller {

    public static void index() {
    	String user = Security.connected();
        render(user);
    }
    
    protected static void logToFile(String operationCode, Long objectId, String logText){
    	String username = Security.connected();
		Logger.info(operationCode + " : username = "+username+ " id = "+objectId + " "+logText);
    }

    protected static void logIPToFile(String IPadress){
    	Logger.info("0_0 IP :"+IPadress);
    }
    
    protected static void logErrorToFile(String operationCode, Long objectId) {
    	String username = Security.connected();
		Logger.error(operationCode + " : username = "+username+ " id = "+objectId);
    }
    
    public static boolean authorize(String operationName){
		String username = Security.connected();
		List<User> users = User.find("byUsername", username).fetch();
		if(users.isEmpty()) {
			return false;
		} 
		User user = users.get(0);
		List<Permission> permissions = user.role.permissions;
		for(Permission p : permissions){
			if(p.name.equals(operationName)){
				return true;
			}
		}
		return false;
	}
	
    
}