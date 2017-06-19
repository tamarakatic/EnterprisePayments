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
		if(!Application.authorize("viewGSTRates")){
			render("errors/401.html");
		}
		List<GSTRate> gstrates = GSTRate.findAll();
		List<GSTType> gsttypes = GSTType.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(gstrates, gsttypes, mode);
	}
	
	public static void create(GSTRate gstrate) {
		if(!Application.authorize("createGSTRate")){
			render("errors/401.html");
		}
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
		if(!Application.authorize("editGSTRate")){
			render("errors/401.html");
		}
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
		if(!Application.authorize("deleteGSTRate")){
			render("errors/401.html");
		}
		if (id != null) {
			GSTRate gstrate = GSTRate.findById(id);
			gstrate.delete();
			Application.logToFile("9_3", gstrate.id, "");
		}
		show("edit");
	}
}
