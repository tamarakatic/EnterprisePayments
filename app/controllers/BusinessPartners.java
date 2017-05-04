package controllers;

import java.util.List;

import models.BusinessPartner;
import models.BusinessYear;
import models.Company;
import play.mvc.Controller;

public class BusinessPartners extends Controller {
	
	public static void show(String mode) {
		List<BusinessPartner> partners = BusinessPartner.findAll();
		List<Company> companies = Company.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(partners, companies, mode);
	}

	public static void create(BusinessPartner partner) {		
		partner.save();
		show("add");
	}

	public static void edit(BusinessPartner partner) {
		partner.save();
		show("edit");
	}

	public static void filter(BusinessPartner partner) {
		List<BusinessPartner> partners = BusinessYear.find("byNameLikeAndAdressLike", 
															partner.name, 
															partner.address).fetch();
		renderTemplate("BusinessPartners/show.html", "edit", partners);
	}

	public static void delete(Long id) {
		if (id != null) {
			BusinessPartner partners = BusinessPartner.findById(id);
			partners.delete();
		}
		show("edit");
	}

}
