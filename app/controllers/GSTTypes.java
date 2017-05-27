package controllers;

import java.util.List;

import models.ArticleGroup;
import models.GSTRate;
import models.GSTType;
import play.Logger;
import play.mvc.Controller;

public class GSTTypes extends Controller{

	public static void show(String mode) {
		List<GSTType> gsttypes = GSTType.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(gsttypes, mode);
	}
	
	public static void create(GSTType gsttype) {
		validation.required("name",gsttype.name);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			gsttype.save();	
			String code = "8_1";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+gsttype.id);
		}
		
		show("add");
	}
	
	public static void edit(GSTType gsttype) {
		validation.required("name",gsttype.name);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			gsttype.save();	
			String code = "8_2";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+gsttype.id);
		}
		
		show("edit");		
	}
	
	public static void filter(GSTType gsttype) {		
		List<GSTType> gsttypes = GSTType.find("name = ?", gsttype.name).fetch();
		renderTemplate("GSTTypes/show.html", "edit", gsttypes);		
	}
	
	public static void delete(Long id) {
		if (id != null){
			GSTType gsttype = GSTType.findById(id);
			List<ArticleGroup> articlegroups = ArticleGroup.find("byGSTType_id", id).fetch();
			List<GSTRate> gstrates = GSTRate.find("byGSTType_id", id).fetch();
			String has_child = "has_child";
			String code = "8_1";
			String user = Security.connected();
			try {
				if (articlegroups != null && !articlegroups.isEmpty() && gstrates != null && !gstrates.isEmpty()) {
					Logger.info(code + " : user = "+user + " id = "+gsttype.id);
					renderTemplate("GSTTypes/show.html", "edit", has_child);
				}
				else {
					gsttype.delete();
					Logger.error(code + " : user = "+user + " id = "+gsttype.id);
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
}
