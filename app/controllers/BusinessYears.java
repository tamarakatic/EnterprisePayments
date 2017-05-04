package controllers;

import java.util.List;

import models.BusinessYear;
import models.Company;
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
		show("add");
	}

	public static void edit(BusinessYear businessyear) {
		businessyear.save();
		show("edit");
	}

	public static void filter(BusinessYear businessyear) {
		List<BusinessYear> years = BusinessYear.find("byYearLikeAndActiveLike", 
													  businessyear.year,
													  businessyear.active).fetch();
		renderTemplate("BusinessYears/show.html", "edit", years);
	}

	public static void delete(Long id) {
		if (id != null) {
			BusinessYear year = BusinessYear.findById(id);
			year.delete();
		}
		show("edit");
	}
}
