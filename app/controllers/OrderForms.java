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
		authorize("viewOrderForms");
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
		authorize("createOrderForm");
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
		authorize("editOrderForm");
		orderForm.save();
		Application.logToFile("4_2", orderForm.id, " - business_partner : "+orderForm.businessPartner.name);
		show("edit");
	}
	
	public static void filter(OrderForm orderForm) {
		List<OrderForm> orderForms = OrderForm.find("byDateOfOrderAndCompanyAndBusinessYearAndBusinessPartner",
				orderForm.dateOfOrder, orderForm.company, orderForm.businessYear,
				orderForm.businessPartner).fetch();
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.findAll();
		List<BusinessPartner> businessPartners = BusinessPartner.findAll();
		renderTemplate("OrderForms/show.html", "edit", orderForms, companies, businessYears, businessPartners);
	}
	
	public static void delete(Long id) {
		authorize("deleteOrderForm");
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
