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
		if(!Application.authorize("viewItems")){
			render("errors/401.html");
		}
		List<Item> items = Item.findAll();
		List<ArticleGroup> articlegroups = ArticleGroup.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(items, articlegroups, mode);
	}

	public static void create(Item item) {	
		checkAuthenticity();
		if(!Application.authorize("createItem")){
			render("errors/401.html");
		}
		validation.required("name", item.name);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			Item i = item.save();
			Application.logToFile("6_1", i.id, " - name : "+i.name);
		}
		
		show("add");
	}

	public static void edit(Item item) {
		checkAuthenticity();
		if(!Application.authorize("editItem")){
			render("errors/401.html");
		}
		validation.required("name", item.name);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} else {
			item.save();
			Application.logToFile("6_2", item.id, " - name : "+item.name);
		}
		
		show("edit");
	}
	
	public static void filter(Item item) {
		checkAuthenticity();
		List<Item> items = Item.find("name = ? or description = ?", 
				item.name,
				item.description).fetch();
		renderTemplate("Items/show.html", "edit", items);
	}

	public static void delete(Long id) {
		if(!Application.authorize("deleteItem")){
			render("errors/401.html");
		}
		if (id != null) {
			Item item = Item.findById(id);
			List<PricelistItem> pricelistitems = PricelistItem.find("byItem_id", id).fetch();
			String has_child = "has_child";
			try {
				if (pricelistitems != null && !pricelistitems.isEmpty()) {
					Application.logErrorToFile("6_3", id);
					renderTemplate("Items/show.html", "edit", has_child);
				}
				else {
					item.delete();
					Application.logToFile("6_3", id, "");
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
	
}
