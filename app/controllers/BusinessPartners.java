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
		authorize("viewBusinessPartners");
		List<BusinessPartner> partners = BusinessPartner.findAll();
		List<Company> companies = Company.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(partners, companies, mode);
	}

	public static void create(BusinessPartner businesspartner) {
		authorize("createBusinessPartner");
		validation.required("name", businesspartner.name);
		validation.required("kind", businesspartner.kind);
		validation.required("account", businesspartner.account);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			String code = "2_1";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+businesspartner.id);
			businesspartner.save();
		}
		
		show("add");
	}

	public static void edit(BusinessPartner businesspartner) {
		authorize("editBusinessPartner");
		validation.required("name", businesspartner.name);
		validation.required("kind", businesspartner.kind);
		validation.required("account", businesspartner.account);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			String code = "2_2";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+businesspartner.id);
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
		authorize("deleteBusinessPartner");
		if (id != null) {
			BusinessPartner partners = BusinessPartner.findById(id);
			partners.delete();
			String code = "2_3";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+id);
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
