package controllers;

import java.util.List;

import models.BusinessYear;
import models.Company;
import models.Permission;
import models.User;
import play.Logger;
import play.mvc.Controller;

public class BusinessYears extends Controller {

	public static void show(String mode) {
		if(!Application.authorize("viewBusinessYears")){
			render("errors/401.html");
		}
		List<BusinessYear> years = BusinessYear.findAll();
		List<Company> companies = Company.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(years, companies, mode);
	}

	public static void create(BusinessYear businessyear) {
		if(!Application.authorize("createBusinessYear")){
			render("errors/401.html");
		}
		BusinessYear by = businessyear.save();	
		Application.logToFile("3_1", by.id, "");
		show("add");
	}

	public static void edit(BusinessYear businessyear) {
		if(!Application.authorize("editBusinessYear")){
			render("errors/401.html");
		}
		businessyear.save();	
		Application.logToFile("3_2", businessyear.id, " - active: "+businessyear.active);
		show("edit");
	}

	public static void filter(BusinessYear businessyear) {
		List<BusinessYear> years = BusinessYear.find("year = ? or active = ?", 
													  businessyear.year,
													  businessyear.active).fetch();
		renderTemplate("BusinessYears/show.html", "edit", years);
	}

	public static void delete(Long id) {
		if(!Application.authorize("deleteBusinessYear")){
			render("errors/401.html");
		}
		if (id != null) {
			BusinessYear year = BusinessYear.findById(id);
			year.delete();
			Application.logToFile("3_2", id, "");
		}
		show("edit");
	}
}
