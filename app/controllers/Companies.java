package controllers;

import java.util.List;

import models.BusinessPartner;
import models.BusinessYear;
import models.Company;
import models.Permission;
import models.User;
import play.Logger;
import play.mvc.Controller;

public class Companies extends Controller {
	
	public static void show(String mode) {
		authorize("viewCompanies");
		List<Company> companies = Company.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(companies, mode);
	}
	
	public static void create(Company company) {
		authorize("createCompany");
		validation.required("name",company.name);
		validation.required("PIB", company.PIB);
		validation.minSize("PIB", company.PIB, 9);
		validation.required("MBR",company.MBR);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			Company c = company.save();
			Application.logToFile("1_1", c.id, " - PIB : "+company.PIB+" MBR : "+company.MBR);
		}
		show("add");
	}
	
	public static void edit(Company company) {
		authorize("editCompany");
		validation.required("name",company.name);
		validation.required("PIB", company.PIB);
		validation.minSize("PIB", company.PIB, 9);
		validation.required("MBR",company.MBR);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			company.save();
			Application.logToFile("1_2", company.id, " - PIB : "+company.PIB+ " MBR : "+company.MBR);
		}
		
		show("edit");		
	}
		
	public static void filter(Company company) {		
		List<Company> companies = Company.find("name = ? or pib = ? or address = ? or mobile = ? or mbr = ?", 
												company.name,
												company.PIB,
												company.address,
												company.mobile,
												company.MBR).fetch();
		renderTemplate("Companies/show.html", "edit", companies);		
	}
	
	public static void delete(Long id) {
		authorize("deleteCompany");
		if (id != null){
			List<BusinessPartner> partners = BusinessPartner.find("byCompany_id", id).fetch();
			List<BusinessYear> years = BusinessYear.find("byCompany_id", id).fetch();
			Company company = Company.findById(id);
			String has_child = "has_child";
			try {
				if (partners != null && !partners.isEmpty() && years != null && !years.isEmpty()) {
					Application.logErrorToFile("1_3", company.id);
					renderTemplate("Companies/show.html", "edit", has_child);
				}
				else {
					company.delete();
					Application.logToFile("1_3", company.id, "");
					show("edit");
				}
					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		show("edit");
	}
	
	public static void business_partner(Long company_id) {
		if (company_id != null) {
			List<BusinessPartner> partners = BusinessPartner.find("byCompany_id", company_id).fetch();
			renderTemplate("BusinessPartners/show.html", "edit", partners, company_id);
		}
		show("edit");
	}
	
	public static void business_year(Long company_id) {
		if (company_id != null) {
			List<BusinessYear> years = BusinessYear.find("byCompany_id", company_id).fetch();
			renderTemplate("BusinessYears/show.html", "edit", years, company_id);
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
