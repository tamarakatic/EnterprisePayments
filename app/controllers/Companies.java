package controllers;

import java.util.List;

import models.Company;
import play.mvc.Controller;

public class Companies extends Controller {
	
	public static void show(String mode) {
		List<Company> companies = Company.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(companies, mode);
	}
	
	public static void create(Company company) {
		company.save();		
		show("add");
	}
	
	public static void edit(Company company) {
		company.save();
		show("edit");		
	}
		
	public static void filter(Company company) {		
		List<Company> companies = Company.find("byNameLike", "%"+ company.name + "%").fetch();
		System.out.println("Company name " + companies);
		renderTemplate("Companies/show.html", "edit", companies);		
	}
	
	public static void delete(Long id) {
		if (id != null){
			Company company = Company.findById(id);
			company.delete();			
		}
		show("edit");
	}
}
