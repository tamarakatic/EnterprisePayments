package controllers;

import java.util.List;

import models.Item;
import models.PricelistItem;
import models.ArticleGroup;
import play.mvc.Controller;

public class Items extends Controller{
	
	public static void show(String mode) {
		List<Item> items = Item.findAll();
		List<ArticleGroup> articlegroups = ArticleGroup.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(items, articlegroups, mode);
	}

	public static void create(Item item) {		
		item.save();
		show("add");
	}

	public static void edit(Item item) {
		item.save();
		show("edit");
	}
	
	public static void filter(Item item) {
		List<Item> items = Item.find("name = ? or description = ?", 
				item.name,
				item.description).fetch();
		renderTemplate("Items/show.html", "edit", items);
	}

	public static void delete(Long id) {
		if (id != null) {
			Item item = Item.findById(id);
			item.delete();
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
