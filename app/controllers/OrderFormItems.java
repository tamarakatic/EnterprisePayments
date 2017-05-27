package controllers;

import java.util.List;

import models.OrderForm;
import models.OrderFormItem;
import models.Item;
import models.Invoice;
import models.InvoiceItem;
import models.Company;
import models.BusinessYear;
import models.BusinessPartner;
import play.Logger;
import play.mvc.Controller;

public class OrderFormItems extends Controller {
	
	public static void show(String mode) {
		List<OrderFormItem> orderFormItems = OrderFormItem.findAll();
		List<OrderForm> orderForms = OrderForm.findAll();
		List<Item> items = Item.findAll();
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		renderTemplate("OrderFormItems/show.html", mode, orderFormItems, orderForms, items);	
	}
	
	public static void create(OrderFormItem orderFormItem) {
		orderFormItem.orderForm = OrderForm.findById(orderFormItem.orderForm.id);
		orderFormItem.save();
		String code = "4_4";
		String user = Security.connected();
		Logger.info(code + " : user = "+user + " id = "+orderFormItem.id);
		show("add");
	}
	
	public static void edit(OrderFormItem orderFormItem) {
		orderFormItem.save();
		String code = "4_5";
		String user = Security.connected();
		Logger.info(code + " : user = "+user + " id = "+orderFormItem.id);
		show("edit");
	}
	
	public static void filter(OrderFormItem orderFormItem) {
		List<OrderFormItem> orderFormItems= OrderFormItem.find("byAmountAndItem",
													orderFormItem.amount, orderFormItem.item).fetch();
		List<OrderForm> orderForms = OrderForm.findAll();
		List<Item> items = Item.findAll();
		renderTemplate("OrderFormItems/show.html", "edit", orderFormItems, orderForms, items);
	}
	
	public static void delete(Long id) {
		if (id != null) {
			OrderFormItem orderFormItem = OrderFormItem.findById(id);
			orderFormItem.delete();
			String code = "4_6";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+orderFormItem.id);
		}
		show("edit");
	}
	
	public static void showNext(String mode, Long id) {
		OrderForm orderForm = OrderForm.findById(id);
		List<OrderFormItem> orderFormItems = OrderFormItem.find("byOrderForm", orderForm).fetch();
		List<Item> items = Item.findAll();
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		renderTemplate("OrderFormItems/showNext.html", mode, orderFormItems, orderForm, items);			
	}

	public static void createNext(OrderFormItem orderFormItem) {
		orderFormItem.orderForm = OrderForm.findById(orderFormItem.orderForm.id);
		orderFormItem.save();
		String code = "4_4";
		String user = Security.connected();
		Logger.info(code + " : user = "+user + " id = "+orderFormItem.id);
		showNext("add", orderFormItem.orderForm.id);
	}
	
	public static void editNext(OrderFormItem orderFormItem) {
		orderFormItem.save();
		String code = "4_5";
		String user = Security.connected();
		Logger.info(code + " : user = "+user + " id = "+orderFormItem.id);
		showNext("edit", orderFormItem.orderForm.id);
	}
	
	public static void deleteNext(Long id, Long orderFormId) {
		if (id != null){
			OrderFormItem orderFormItem = OrderFormItem.findById(id);
			orderFormItem.delete();		
			String code = "4_6";
			String user = Security.connected();
			Logger.info(code + " : user = "+user + " id = "+orderFormItem.id);
		}
		showNext("edit", orderFormId);
	}
	
	public static void filterNext(OrderFormItem orderFormItem) {		
		List<OrderFormItem> orderFormItems = OrderFormItem.find("byOrderFormAndAmount",
				orderFormItem.orderForm,
				orderFormItem.amount
				).fetch();
		OrderForm orderForm = orderFormItem.orderForm;
		renderTemplate("OrderFormItems/showNext.html", "edit", orderFormItems, orderForm);	
	}
	
	public static void generateInvoice(Long id) {
		OrderForm orderForm = OrderForm.findById(id);
		List<OrderFormItem> orderFormItems = OrderFormItem.find("byOrderForm", orderForm).fetch();
		
		int num = 0;
		List<Invoice> invoicesInYear = Invoice.find("byBusinessYear", orderForm.businessYear).fetch(); //invoice is not created yet
		for(Invoice inv : invoicesInYear) {
			if(inv.number > num) {
				num = inv.number;
			}
		}
		
		Invoice invoice = new Invoice(orderForm.dateOfOrder, ++num, orderForm.dateOfOrder,
				0.0, 0.0, 0.0, orderForm.company, orderForm.businessYear, orderForm.businessPartner);
		for (OrderFormItem ofi : orderFormItems) {
			InvoiceItem invoiceItem = new InvoiceItem(ofi.amount, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, invoice, ofi.item);
			invoiceItem = InvoiceItems.calculate(invoiceItem);
	    	invoiceItem.save();		
		}
		List<Invoice> invoices = Invoice.find("byId", invoice.id).fetch();
		String mode = "edit";
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.findAll();
		List<BusinessPartner> businessPartners = BusinessPartner.findAll();
		
		String code = "4_7";
		String user = Security.connected();
		Logger.info(code + " : user = "+user + " id = "+id);
		
		renderTemplate("invoices/show.html", mode, invoices, companies, businessYears, businessPartners);
	}
}
