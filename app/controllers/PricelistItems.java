package controllers;

import java.util.List;

import models.Item;
import models.Pricelist;
import models.PricelistItem;
import play.mvc.Controller;

public class PricelistItems extends Controller{
	
	public static void show(String mode) {
		List<PricelistItem> pricelistsItems = PricelistItem.findAll();
		List<Item> items = Item.findAll();
		List<Pricelist> pricelists =Pricelist.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(pricelistsItems, items, pricelists, mode);
	}

	public static void create(PricelistItem pricelistItem) {		
		pricelistItem.save();
		show("add");
	}

	public static void edit(PricelistItem pricelistItem) {
		pricelistItem.save();
		show("edit");
	}
	
	public static void filter(PricelistItem pricelistItem) {
		List<PricelistItem> pricelistItems = PricelistItem.find("price = ?", 
				pricelistItem.price).fetch();
		renderTemplate("PricelistItems/show.html", "edit", pricelistItems);
	}

	public static void delete(Long id) {
		if (id != null) {
			PricelistItem pricelistItem = PricelistItem.findById(id);
			pricelistItem.delete();
		}
		show("edit");
	}

}
