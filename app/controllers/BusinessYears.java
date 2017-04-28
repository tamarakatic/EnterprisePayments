package controllers;

import java.util.List;

import models.BusinessYear;
import play.mvc.Controller;

public class BusinessYears extends Controller {

	public static void show(String mode) {
		List<BusinessYear> years = BusinessYear.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(years, mode);
	}

	public static void create(BusinessYear year) {
		year.save();
		show("add");
	}

	public static void edit(BusinessYear year) {
		year.save();
		show("edit");
	}

	public static void filter(BusinessYear year) {
		List<BusinessYear> years = BusinessYear
				.find("byYearLikeAndActiveLike", "%" + year.year + "%", 
												 "%" + year.active + "%").fetch();
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
