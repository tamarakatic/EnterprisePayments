package controllers;

import java.util.List;

import models.GSTRate;
import models.GSTType;
import models.Permission;
import models.User;
import play.Logger;
import play.mvc.Controller;

public class GSTRates extends Controller {
	
	public static void show(String mode) {
		authorize("viewGSTRates");
		List<GSTRate> gstrates = GSTRate.findAll();
		List<GSTType> gsttypes = GSTType.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(gstrates, gsttypes, mode);
	}
	
	public static void create(GSTRate gstrate) {
		authorize("createGSTRate");
		validation.required("GSTPercent", gstrate.GSTPercent);
		validation.min("GSTPercent", gstrate.GSTPercent, 0.01);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			GSTRate g = gstrate.save();
			Application.logToFile("9_1", g.id, " - percent : "+g.GSTPercent);
		}
		
		show("add");
	}

	public static void edit(GSTRate gstrate) {
		authorize("editGSTRate");
		validation.required("GSTPercent", gstrate.GSTPercent);
		validation.min("GSTPercent", gstrate.GSTPercent, 0.01);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			gstrate.save();
			Application.logToFile("9_2", gstrate.id, " - percent : "+gstrate.GSTPercent);
		}
		
		show("edit");
	}
	
	public static void filter(GSTRate gstrate) {
		List<GSTRate> gstrates = GSTRate.find("date = ? or GSTPercent = ?", 
											   gstrate.date,
											   gstrate.GSTPercent).fetch();
		renderTemplate("GSTRates/show.html", "edit", gstrates);
	}

	public static void delete(Long id) {
		authorize("deleteGSTRate");
		if (id != null) {
			GSTRate gstrate = GSTRate.findById(id);
			gstrate.delete();
			Application.logToFile("9_3", gstrate.id, "");
		}
		show("edit");
	}
	
	private static void authorize(String operationName){
		String username = Security.connected();
		List<User> users = User.find("byUsername", username).fetch();
		if(users.isEmpty()) {
			render("errors/401.html");
		} 
		User user = users.get(0);
		boolean check = false;
		List<Permission> permissions = user.role.permissions;
		for(Permission p : permissions){
			if(p.name.equals(operationName)){
				check = true;
				break;
			}
		}
		if(!check) {
			render("errors/401.html");
		}
	}

}
