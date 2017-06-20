package controllers;

import java.util.List;

import models.Item;
import models.Permission;
import models.Pricelist;
import models.PricelistItem;
import models.User;
import play.Logger;
import play.mvc.Controller;

public class PricelistItems extends Controller{
	
	public static void show(String mode) {
		if(!Application.authorize("viewPriceListItems")){
			render("errors/401.html");
		}
		List<PricelistItem> pricelistitems = PricelistItem.findAll();
		List<Item> items = Item.findAll();		
		List<Pricelist> pricelists = Pricelist.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(pricelistitems, items, pricelists, mode);
	}
	
	public static void create(PricelistItem pricelistitem) {	
		checkAuthenticity();
		if(!Application.authorize("createPriceListItem")){
			render("errors/401.html");
		}
		validation.required("price", pricelistitem.price);
		validation.min("price", pricelistitem.price, 0.01);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			PricelistItem p = pricelistitem.save();
			Application.logToFile("10_4", p.id, " - price : "+pricelistitem.price);
		}
		
		show("add");
	}

	public static void edit(PricelistItem pricelistitem) {
		checkAuthenticity();
		if(!Application.authorize("editPriceListItem")){
			render("errors/401.html");
		}
		validation.required("price", pricelistitem.price);
		validation.min("price", pricelistitem.price, 0.01);		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
		} 
		else {
			pricelistitem.save();
			Application.logToFile("10_5", pricelistitem.id, " - price : "+pricelistitem.price);
		}
		
		show("edit");
	}
	
	public static void filter(PricelistItem pricelistitem) {
		checkAuthenticity();
		List<PricelistItem> pricelistitems = PricelistItem.find("price = ?", 
				pricelistitem.price).fetch();
		renderTemplate("PricelistItems/show.html", "edit", pricelistitems);
	}

	public static void delete(Long id) {
		if(!Application.authorize("deletePriceListItem")){
			render("errors/401.html");
		}
		if (id != null) {
			PricelistItem pricelistitem = PricelistItem.findById(id);
			pricelistitem.delete();
			Application.logToFile("10_6", id, "");
		}
		show("edit");
	}

}
