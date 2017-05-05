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

	public static void create(BusinessPartner businesspartner) {		
		businesspartner.save();
		show("add");
	}

	public static void edit(BusinessPartner businesspartner) {
		businesspartner.save();
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
		if (id != null) {
			BusinessPartner partners = BusinessPartner.findById(id);
			partners.delete();
		}
		show("edit");
	}

}
