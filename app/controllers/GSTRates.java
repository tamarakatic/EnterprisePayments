package controllers;

import java.util.List;

import models.GSTRate;
import models.GSTType;
import play.mvc.Controller;

public class GSTRates extends Controller {
	
	public static void show(String mode) {
		List<GSTRate> gstrates = GSTRate.findAll();
		List<GSTType> gsttypes = GSTType.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(gstrates, gsttypes, mode);
	}
	
	public static void create(GSTRate gstrate) {
		validation.required("GSTPercent", gstrate.GSTPercent);
		validation.min("GSTPercent", gstrate.GSTPercent, 0.01);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else
			gstrate.save();
		
		show("add");
	}

	public static void edit(GSTRate gstrate) {
		validation.required("GSTPercent", gstrate.GSTPercent);
		validation.min("GSTPercent", gstrate.GSTPercent, 0.01);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else
			gstrate.save();
		
		show("edit");
	}
	
	public static void filter(GSTRate gstrate) {
		List<GSTRate> gstrates = GSTRate.find("date = ? or GSTPercent = ?", 
											   gstrate.date,
											   gstrate.GSTPercent).fetch();
		renderTemplate("GSTRates/show.html", "edit", gstrates);
	}

	public static void delete(Long id) {
		if (id != null) {
			GSTRate gstrate = GSTRate.findById(id);
			gstrate.delete();
		}
		show("edit");
	}

}
