package controllers;

import java.util.List;

import models.PricelistItem;
import models.GSTType;
import models.Pricelist;
import play.mvc.Controller;

public class Pricelists extends Controller{
	
	public static void show(String mode) {
		List<Pricelist> pricelists = Pricelist.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(pricelists, mode);
	}
	
	public static void create(Pricelist pricelist) {
		pricelist.save();		
		show("add");
	}
	
	public static void edit(Pricelist pricelist) {
		pricelist.save();
		show("edit");		
	}
	
	public static void filter(Pricelist pricelis) {		
		List<Pricelist> pricelists = Pricelist.find("validationDate = ?", pricelis.validationDate).fetch();
		renderTemplate("Pricelist/show.html", "edit", pricelists);		
	}
	
	public static void delete(Long id) {
		if (id != null){
			Pricelist pricelist = Pricelist.findById(id);
			pricelist.delete();			
		}
		show("edit");
	}
	
	public static void pricelist_item(Long pricelist_id) {
		if (pricelist_id != null) {
			List<PricelistItem> pricelistItems = PricelistItem.find("byPricelist_id", pricelist_id).fetch();
			renderTemplate("PricelistItem/show.html", "edit", pricelistItems, pricelist_id);
		}
		show("edit");
	}

}
