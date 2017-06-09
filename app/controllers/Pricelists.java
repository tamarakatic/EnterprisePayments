package controllers;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import models.PricelistItem;
import models.GSTType;
import models.Item;
import models.Pricelist;
import play.mvc.Controller;
import play.mvc.With;

@With(HttpsController.class)
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
	
	public static void filter(Pricelist pricelist) {		
		List<Pricelist> pricelists = Pricelist.find("validationDate = ?", pricelist.validationDate).fetch();
		renderTemplate("Pricelists/show.html", "edit", pricelists);		
	}
	
	public static void delete(Long id) {
		if (id != null){
			Pricelist pricelist = Pricelist.findById(id);
			List<PricelistItem> pricelistitems = PricelistItem.find("byPricelist_id", id).fetch();
			String has_child = "has_child";
			try {
				if (pricelistitems != null && !pricelistitems.isEmpty()) {
					renderTemplate("Pricelists/show.html", "edit", has_child);
				}
				else {
					pricelist.delete();
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
			}			
			redirect("/PricelistItems/show?");
		}    
		show("edit");
	}

}
