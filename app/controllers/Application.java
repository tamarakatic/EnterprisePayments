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
    
    static void logToFile(String operationCode, Long objectId, String logText){
    	String username = Security.connected();
		Logger.info(operationCode + " : username = "+username+ " id = "+objectId + " "+logText);
    }

    static void logErrorToFile(String operationCode, Long objectId) {
    	String username = Security.connected();
		Logger.error(operationCode + " : username = "+username+ " id = "+objectId);
    }
    
}