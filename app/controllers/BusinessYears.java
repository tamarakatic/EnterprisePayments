package controllers;

import java.util.List;

import models.BusinessYear;
import models.Company;
import play.Logger;
import play.mvc.Controller;

public class BusinessYears extends Controller {

	public static void show(String mode) {
		List<BusinessYear> years = BusinessYear.findAll();
		List<Company> companies = Company.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(years, companies, mode);
	}

	public static void create(BusinessYear businessyear) {
		businessyear.save();	
		String code = "3_1";
		String user = Security.connected();
		Logger.info(code + " : user = "+user + " id = "+businessyear.id);
		show("add");
	}

	public static void edit(BusinessYear businessyear) {
		businessyear.save();	
		String code = "3_2";
		String user = Security.connected();
		Logger.info(code + " : user = "+user + " id = "+businessyear.id);
		show("edit");
	}

	public static void filter(BusinessYear businessyear) {
		List<BusinessYear> years = BusinessYear.find("year = ? or active = ?", 
													  businessyear.year,
													  businessyear.active).fetch();
		renderTemplate("BusinessYears/show.html", "edit", years);
	}

	public static void delete(Long id) {
		if (id != null) {
			BusinessYear year = BusinessYear.findById(id);
			year.delete();
			String code = "3_3";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+id);
		}
		show("edit");
	}
}
