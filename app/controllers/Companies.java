package controllers;

import java.util.List;

import models.BusinessPartner;
import models.BusinessYear;
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
		List<Company> companies = Company.find("name = ? or pib = ? or address = ? or mobile = ? or jmbg = ?", 
												company.name,
												company.PIB,
												company.address,
												company.mobile,
												company.JMBG).fetch();
		renderTemplate("Companies/show.html", "edit", companies);		
	}
	
	public static void delete(Long id) {
		if (id != null){
			Company company = Company.findById(id);
			company.delete();			
		}
		show("edit");
	}
	
	public static void business_partner(Long id) {
		if (id != null) {
			List<BusinessPartner> businessPartners = BusinessPartner.find("byCompany_id", id).fetch();
			renderTemplate("Companies/business_partners.html" ,businessPartners);
		}
		show("edit");
	}
	
	public static void business_year(Long id) {
		if (id != null) {
			List<BusinessYear> businessYears = BusinessYear.find("byCompany_id", id).fetch();
			renderTemplate("Companies/business_years.html" ,businessYears);
		}
		show("edit");
	}
}
