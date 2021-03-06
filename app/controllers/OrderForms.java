package controllers;

import java.util.List;

import models.BusinessPartner;
import models.BusinessYear;
import models.Company;
import models.OrderForm;
import models.OrderFormItem;
import models.Permission;
import models.User;
import models.Item;
import play.Logger;
import play.mvc.Controller;

public class OrderForms extends Controller {
	
	public static void show(String mode) {
		if(!Application.authorize("viewOrderForms")){
			render("errors/401.html");
		}
		List<OrderForm> orderForms = OrderForm.findAll();
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.findAll();
		List<BusinessPartner> businessPartners = BusinessPartner.findAll();
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		if(session.get("role").equals("bussines partner")){
			User user = (User) User.find("byUsername", Security.connected()).fetch().get(0);
			businessPartners = BusinessPartner.find("byName", user.username).fetch();
			if(businessPartners.isEmpty()){
				render("errors/401.html");
			}
			BusinessPartner bp = businessPartners.get(0);
			orderForms = OrderForm.find("byBusinessPartner", bp).fetch();
		}
		renderTemplate("OrderForms/show.html", mode, orderForms, companies, businessYears, businessPartners);
	}
	
	public static void create(OrderForm orderForm) {
		checkAuthenticity();
		if(!Application.authorize("createOrderForm")){
			render("errors/401.html");
		}
		Company company = Company.findById(orderForm.company.id);
		orderForm.company = company;
		orderForm.businessYear = BusinessYear.findById(orderForm.businessYear.id);
		orderForm.businessPartner = BusinessPartner.findById(orderForm.businessPartner.id);
		int num = 0;
		List<OrderForm> orderFormsInYear = OrderForm.find("byBusinessYear", orderForm.businessYear).fetch();
		for(OrderForm of : orderFormsInYear) {
			if(of.numberOfOrder > num) {
				num = of.numberOfOrder;
			}
		}
		orderForm.numberOfOrder = ++num;
		OrderForm o = orderForm.save();
		Application.logToFile("4_1", o.id, " - business_partner : "+o.businessPartner.name);
		show("add");
	}
	
	public static void edit(OrderForm orderForm) {
		checkAuthenticity();
		if(!Application.authorize("editOrderForm")){
			render("errors/401.html");
		}
		orderForm.save();
		Application.logToFile("4_2", orderForm.id, " - business_partner : "+orderForm.businessPartner.name);
		show("edit");
	}
	
	public static void filter(OrderForm orderForm) {
		checkAuthenticity();
		List<BusinessPartner> businessPartners = null;
		if(session.get("role").equals("bussines partner")){
			User user = (User) User.find("byUsername", Security.connected()).fetch().get(0);
			businessPartners = BusinessPartner.find("byName", user.username).fetch();
			BusinessPartner bp = (BusinessPartner) BusinessPartner.find("byName", user.username).fetch().get(0);
			orderForm.businessPartner = bp;
		}
		List<OrderForm> orderForms = OrderForm.find("byDateOfOrderAndCompanyAndBusinessYearAndBusinessPartner",
				orderForm.dateOfOrder, orderForm.company, orderForm.businessYear,
				orderForm.businessPartner).fetch();
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.findAll();
		if(!session.get("role").equals("bussines partner")) {
			businessPartners = BusinessPartner.findAll();
		}
		renderTemplate("OrderForms/show.html", "edit", orderForms, companies, businessYears, businessPartners);
	}
	
	public static void delete(Long id) {
		if(!Application.authorize("deleteOrderForm")){
			render("errors/401.html");
		}
		if (id != null) {
			OrderForm orderForm = OrderForm.findById(id);
			if (orderForm.orderFormItems.isEmpty()) {
				orderForm.delete();
				Application.logToFile("4_3", id, "");
			}
			else {
				List<OrderForm> orderForms = OrderForm.findAll();
				String mode = "edit";
				boolean hasChildren = true;
				List<Company> companies = Company.findAll();
				List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
				List<BusinessPartner> businessPartners = BusinessPartner.findAll();
				Application.logErrorToFile("4_3", id);
				
				if(session.get("role").equals("bussines partner")){
					User user = (User) User.find("byUsername", Security.connected()).fetch().get(0);
					businessPartners = BusinessPartner.find("byName", user.username).fetch();
					if(businessPartners.isEmpty()){
						render("errors/401.html");
					}
					BusinessPartner bp = businessPartners.get(0);
					orderForms = OrderForm.find("byBusinessPartner", bp).fetch();
				}
				renderTemplate("OrderForms/show.html", mode, orderForms, companies, businessYears, businessPartners, hasChildren);	
			}
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
