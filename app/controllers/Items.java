package controllers;

import java.util.List;

import models.Item;
import models.Permission;
import models.PricelistItem;
import models.User;
import models.ArticleGroup;
import play.Logger;
import play.mvc.Controller;

public class Items extends Controller{
	
	public static void show(String mode) {
		authorize("viewItems");
		List<Item> items = Item.findAll();
		List<ArticleGroup> articlegroups = ArticleGroup.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(items, articlegroups, mode);
	}

	public static void create(Item item) {	
		authorize("createItem");
		validation.required("name", item.name);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			item.save();
			String code = "6_1";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+item.id);
		}
		
		show("add");
	}

	public static void edit(Item item) {
		authorize("editItem");
		validation.required("name", item.name);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			item.save();
			String code = "6_2";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+item.id);
		}
		
		show("edit");
	}
	
	public static void filter(Item item) {
		List<Item> items = Item.find("name = ? or description = ?", 
				item.name,
				item.description).fetch();
		renderTemplate("Items/show.html", "edit", items);
	}

	public static void delete(Long id) {
		authorize("deleteItem");
		if (id != null) {
			Item item = Item.findById(id);
			List<PricelistItem> pricelistitems = PricelistItem.find("byItem_id", id).fetch();
			String has_child = "has_child";
			try {
				if (pricelistitems != null && !pricelistitems.isEmpty()) {
					String code = "6_3";
					String user = Security.connected();
					Logger.error(code + " : user = "+user + " id = "+item.id);
					renderTemplate("Items/show.html", "edit", has_child);
				}
				else {
					item.delete();
					String code = "6_3";
					String user = Security.connected();
					Logger.info(code + " : user = "+user + " id = "+item.id);
					show("edit");
				}					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		show("edit");
	}
	
	public static void pricelist_item(Long item_id) {
		if (item_id != null) {
			List<PricelistItem> pricelistitems = PricelistItem.find("byItem_id", item_id).fetch();
			renderTemplate("PricelistItems/show.html", "edit", pricelistitems, item_id);
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
