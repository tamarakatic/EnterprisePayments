package controllers;

import java.util.List;

import models.BusinessPartner;
import models.BusinessYear;
import models.Company;
import models.Permission;
import models.User;
import play.Logger;
import play.mvc.Controller;

public class BusinessPartners extends Controller {
	
	public static void show(String mode) {
		if(!Application.authorize("viewBusinessPartners")){
			render("errors/401.html");
		}
		List<BusinessPartner> partners = BusinessPartner.findAll();
		List<Company> companies = Company.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(partners, companies, mode);
	}

	public static void create(BusinessPartner businesspartner) {
		if(!Application.authorize("createBusinessPartner")){
			render("errors/401.html");
		}
		validation.required("name", businesspartner.name);
		validation.required("kind", businesspartner.kind);
		validation.required("account", businesspartner.account);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			BusinessPartner bp = businesspartner.save();
			Application.logToFile("2_1", bp.id, " - account : "+businesspartner.account);
		}
		
		show("add");
	}

	public static void edit(BusinessPartner businesspartner) {
		if(!Application.authorize("editBusinessPartner")){
			render("errors/401.html");
		}
		validation.required("name", businesspartner.name);
		validation.required("kind", businesspartner.kind);
		validation.required("account", businesspartner.account);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			Application.logToFile("2_2", businesspartner.id, " - account : "+businesspartner.account);
			businesspartner.save();
		}
		
		show("edit");
	}

	public static void filter(BusinessPartner businesspartner) {
		List<BusinessPartner> partners = BusinessPartner.find("name = ? or address = ? or kind = ? or "
															+ "mobile = ? or email = ? or account = ?", 
															businesspartner.name, 
															businesspartner.address,
															businesspartner.kind,
															businesspartner.mobile,
															businesspartner.email,
															businesspartner.account).fetch();
		renderTemplate("BusinessPartners/show.html", "edit", partners);
	}

	public static void delete(Long id) {
		if(!Application.authorize("deleteBusinessPartner")){
			render("errors/401.html");
		}
		if (id != null) {
			BusinessPartner partners = BusinessPartner.findById(id);
			partners.delete();
			Application.logToFile("2_3", id, "");
		}
		show("edit");
	}
}
