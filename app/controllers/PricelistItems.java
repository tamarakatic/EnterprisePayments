package controllers;

import java.util.List;

import models.Item;
import models.Pricelist;
import models.PricelistItem;
import play.Logger;
import play.mvc.Controller;

public class PricelistItems extends Controller{
	
	public static void show(String mode) {
		List<PricelistItem> pricelistitems = PricelistItem.findAll();
		List<Item> items = Item.findAll();		
		List<Pricelist> pricelists = Pricelist.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(pricelistitems, items, pricelists, mode);
	}
	
	public static void create(PricelistItem pricelistitem) {	
		validation.required("price", pricelistitem.price);
		validation.min("price", pricelistitem.price, 0.01);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			pricelistitem.save();
			String code = "10_4";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+pricelistitem.id);
		}
		
		show("add");
	}

	public static void edit(PricelistItem pricelistitem) {
		validation.required("price", pricelistitem.price);
		validation.min("price", pricelistitem.price, 0.01);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			pricelistitem.save();
			String code = "10_5";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+pricelistitem.id);
		}
		
		show("edit");
	}
	
	public static void filter(PricelistItem pricelistitem) {
		List<PricelistItem> pricelistitems = PricelistItem.find("price = ?", 
				pricelistitem.price).fetch();
		renderTemplate("PricelistItems/show.html", "edit", pricelistitems);
	}

	public static void delete(Long id) {
		if (id != null) {
			PricelistItem pricelistitem = PricelistItem.findById(id);
			pricelistitem.delete();
			String code = "10_6";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+pricelistitem.id);
		}
		show("edit");
	}

}
