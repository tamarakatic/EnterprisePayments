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
		authorize("viewPriceListItems");
		List<PricelistItem> pricelistitems = PricelistItem.findAll();
		List<Item> items = Item.findAll();		
		List<Pricelist> pricelists = Pricelist.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(pricelistitems, items, pricelists, mode);
	}
	
	public static void create(PricelistItem pricelistitem) {	
		authorize("createPriceListItem");
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
		authorize("editPriceListItem");
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
		authorize("deletePriceListItem");
		if (id != null) {
			PricelistItem pricelistitem = PricelistItem.findById(id);
			pricelistitem.delete();
			String code = "10_6";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+pricelistitem.id);
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
