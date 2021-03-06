package controllers;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import models.PricelistItem;
import models.User;
import models.GSTType;
import models.Item;
import models.Permission;
import models.Pricelist;
import play.Logger;
import play.mvc.Controller;

public class Pricelists extends Controller{
	
	public static void show(String mode) {
		if(!Application.authorize("viewPriceLists")){
			render("errors/401.html");
		}
		List<Pricelist> pricelists = Pricelist.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(pricelists, mode);
	}
	
	public static void create(Pricelist pricelist) {
		checkAuthenticity();
		if(!Application.authorize("createPriceList")){
			render("errors/401.html");
		}
		Pricelist p = pricelist.save();	
		Application.logToFile("10_1", p.id, " - date : "+pricelist.validationDate);
		show("add");
	}
	
	public static void edit(Pricelist pricelist) {
		checkAuthenticity();
		if(!Application.authorize("editPriceList")){
			render("errors/401.html");
		}
		pricelist.save();
		Application.logToFile("10_2", pricelist.id, " - date : "+pricelist.validationDate);
		show("edit");		
	}
	
	public static void filter(Pricelist pricelist) {	
		checkAuthenticity();
		List<Pricelist> pricelists = Pricelist.find("validationDate = ?", pricelist.validationDate).fetch();
		renderTemplate("Pricelists/show.html", "edit", pricelists);		
	}
	
	public static void delete(Long id) {
		if(!Application.authorize("deletePriceList")){
			render("errors/401.html");
		}
		if (id != null){
			Pricelist pricelist = Pricelist.findById(id);
			List<PricelistItem> pricelistitems = PricelistItem.find("byPricelist_id", id).fetch();
			String has_child = "has_child";
			try {
				if (pricelistitems != null && !pricelistitems.isEmpty()) {
					Application.logErrorToFile("10_1", pricelist.id);
					renderTemplate("Pricelists/show.html", "edit", has_child);
				}
				else {
					pricelist.delete();
					Application.logToFile("10_1", pricelist.id, "");
					show("edit");
				}					
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		show("edit");
	}
	
	public static void pricelist_item(Long pricelist_id) {
		if (pricelist_id != null) {
			List<PricelistItem> pricelistitems = PricelistItem.find("byPricelist_id", pricelist_id).fetch();
			renderTemplate("PricelistItems/show.html", "edit", pricelistitems, pricelist_id);
		}
		show("edit");
	}
		
	public static void change_price_list(Long pricelist_id, Integer percentage) {
		if(!Application.authorize("copyPriceList")){
			render("errors/401.html");
		}
		if (pricelist_id != null) {
			Pricelist pricelist = Pricelist.findById(pricelist_id);
			DecimalFormat decimalFormat = new DecimalFormat(".##");
			pricelist.validationDate = new Date();
			pricelist.save();
			List<PricelistItem> pricelistItems = PricelistItem.find("byPricelist_id", pricelist_id).fetch();
			for (PricelistItem pricelistItem : pricelistItems) {
				PricelistItem priceListItemId = PricelistItem.findById(pricelistItem.id);
				if (percentage < 100) {
					priceListItemId.price = Double.parseDouble(decimalFormat.format(priceListItemId.price - 
							(priceListItemId.price * (percentage / 100.0f))));
				} else {
					priceListItemId.price = Double.parseDouble(decimalFormat.format(priceListItemId.price * (percentage / 100.0f)));
				}				
				priceListItemId.save();		
				Application.logToFile("10_7", pricelist_id, " - percentage : "+percentage);
			}			
			redirect("/PricelistItems/show?");
		}    
		show("edit");
	}
}
