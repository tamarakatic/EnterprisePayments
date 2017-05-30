package controllers;

import java.util.List;

import models.ArticleGroup;
import models.GSTType;
import models.Item;
import models.Permission;
import models.User;
import play.Logger;
import play.mvc.Controller;

public class ArticleGroups extends Controller{
	
	public static void show(String mode) {
		authorize("viewArticleGroups");
		List<ArticleGroup> articlegroups = ArticleGroup.findAll();
		List<GSTType> gsttypes = GSTType.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(articlegroups, gsttypes, mode);
	}
	
	public static void create(ArticleGroup articlegroup) {	
		authorize("createArticleGroup");
		validation.required("name", articlegroup.name);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			ArticleGroup a = articlegroup.save();
			Application.logToFile("7_1", a.id, "");
		}
		
		show("add");
	}

	public static void edit(ArticleGroup articlegroup) {
		authorize("editArticleGroup");
		validation.required("name", articlegroup.name);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			articlegroup.save();
			Application.logToFile("7_2", articlegroup.id, "- name : "+articlegroup.name);
		}
		
		show("edit");
	}
	
	public static void filter(ArticleGroup articlegroup) {
		List<ArticleGroup> articlegroups = ArticleGroup.find("name = ?",
				articlegroup.name).fetch();
		renderTemplate("ArticleGroups/show.html", "edit", articlegroups);
	}

	public static void delete(Long id) {
		authorize("deleteArticleGroup");
		if (id != null) {
			ArticleGroup articlegroup = ArticleGroup.findById(id);
			List<Item> items = Item.find("byArticlegroup_id", id).fetch();			
			String has_child = "has_child";
			try {
				if (items != null && !items.isEmpty()) {
					Application.logErrorToFile("7_3", id);
					renderTemplate("ArticleGroups/show.html", "edit", has_child);
				}
				else {
					articlegroup.delete();
					Application.logToFile("7_3", id, "");
					show("edit");
				}
					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		show("edit");
	}
	
	public static void item(Long articlegroup_id) {
		if (articlegroup_id != null) {
			List<Item> items = Item.find("byArticlegroup_id", articlegroup_id).fetch();
			renderTemplate("Items/show.html", "edit", items, articlegroup_id);
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
