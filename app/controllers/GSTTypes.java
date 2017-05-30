package controllers;

import java.util.List;

import models.ArticleGroup;
import models.GSTRate;
import models.GSTType;
import models.Permission;
import models.User;
import play.Logger;
import play.mvc.Controller;

public class GSTTypes extends Controller{

	public static void show(String mode) {
		authorize("viewGSTTypes");
		List<GSTType> gsttypes = GSTType.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(gsttypes, mode);
	}
	
	public static void create(GSTType gsttype) {
		authorize("createGSTType");
		validation.required("name",gsttype.name);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			GSTType g = gsttype.save();	
			Application.logToFile("8_1", g.id, "");
		}
		
		show("add");
	}
	
	public static void edit(GSTType gsttype) {
		authorize("editGSTType");
		validation.required("name",gsttype.name);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			gsttype.save();	
			Application.logToFile("8_2", gsttype.id, "");
		}
		
		show("edit");		
	}
	
	public static void filter(GSTType gsttype) {		
		List<GSTType> gsttypes = GSTType.find("name = ?", gsttype.name).fetch();
		renderTemplate("GSTTypes/show.html", "edit", gsttypes);		
	}
	
	public static void delete(Long id) {
		authorize("deleteGSTType");
		if (id != null){
			GSTType gsttype = GSTType.findById(id);
			List<ArticleGroup> articlegroups = ArticleGroup.find("byGSTType_id", id).fetch();
			List<GSTRate> gstrates = GSTRate.find("byGSTType_id", id).fetch();
			String has_child = "has_child";
			try {
				if (articlegroups != null && !articlegroups.isEmpty() && gstrates != null && !gstrates.isEmpty()) {
					Application.logToFile("8_3", id, "");
					renderTemplate("GSTTypes/show.html", "edit", has_child);
				}
				else {
					gsttype.delete();
					Application.logErrorToFile("8_3", id);
					show("edit");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		show("edit");
	}
	
	public static void article_group(Long gsttype_id) {
		if (gsttype_id != null) {
			List<ArticleGroup> articlegroups = ArticleGroup.find("byGSTType_id", gsttype_id).fetch();
			renderTemplate("ArticleGroups/show.html", "edit", articlegroups, gsttype_id);
		}
		show("edit");
	}
	
	public static void gstrate(Long gsttype_id) {
		if (gsttype_id != null) {
			List<GSTRate> gstrates = GSTRate.find("byGSTType_id", gsttype_id).fetch();
			renderTemplate("GSTRates/show.html", "edit", gstrates, gsttype_id);
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
