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
		authorize("viewBusinessYears");
		List<BusinessYear> years = BusinessYear.findAll();
		List<Company> companies = Company.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(years, companies, mode);
	}

	public static void create(BusinessYear businessyear) {
		authorize("createBusinessYear");
		BusinessYear by = businessyear.save();	
		Application.logToFile("3_1", by.id, "");
		show("add");
	}

	public static void edit(BusinessYear businessyear) {
		authorize("editBusinessYear");
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
		authorize("deleteBusinessYear");
		if (id != null) {
			BusinessYear year = BusinessYear.findById(id);
			year.delete();
			Application.logToFile("3_2", id, "");
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
