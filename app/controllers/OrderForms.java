package controllers;

import java.util.List;

import models.BusinessPartner;
import models.BusinessYear;
import models.Company;
import models.OrderForm;
import models.OrderFormItem;
import models.Item;
import play.mvc.Controller;

public class OrderForms extends Controller {
	
	public static void show(String mode) {
		List<OrderForm> orderForms = OrderForm.findAll();
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.findAll();
		List<BusinessPartner> businessPartners = BusinessPartner.findAll();
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		renderTemplate("OrderForms/show.html", mode, orderForms, companies, businessYears, businessPartners);
	}
	
	public static void create(OrderForm orderForm) {
		Company company = Company.findById(orderForm.company.id);
		orderForm.company = company;
		orderForm.businessYear = BusinessYear.findById(orderForm.businessYear.id);
		orderForm.businessPartner = BusinessPartner.findById(orderForm.businessPartner.id);
		orderForm.save();
		show("add");
	}
	
	public static void edit(OrderForm orderForm) {
		orderForm.save();
		show("edit");
	}
	
	public static void filter(OrderForm orderForm) {
		List<OrderForm> orderForms = OrderForm.find("dateOfOrder = ? or numberOfOrder = ?",
													orderForm.dateOfOrder, orderForm.numberOfOrder).fetch();
		renderTemplate("OrderForms/show.html", "edit", orderForms);
	}
	
	public static void delete(Long id) {
		if (id != null) {
			OrderForm orderForm = OrderForm.findById(id);
			orderForm.delete();
		}
		show("edit");
	}
	
	public static void nextForm(Long id) {
		if (id != null) {
			OrderForm orderForm = OrderForm.findById(id);
			List<OrderFormItem> orderFormItems = OrderFormItem.find("byOrderForm", orderForm).fetch();
			List<Item> items = Item.findAll();
			renderTemplate("OrderFormItems/showNext.html", "edit", orderFormItems, orderForm, items);
		}
		show("edit");
	}
	
}
