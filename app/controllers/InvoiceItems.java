package controllers;

import java.util.List;

import models.Invoice;
import models.InvoiceItem;
import play.mvc.Controller;

public class InvoiceItems extends Controller{

	public static void show(String mode){
		List<InvoiceItem> invoiceItems = InvoiceItem.findAll();
		List<Invoice> invoices = Invoice.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		renderTemplate("InvoiceItems/show.html", mode, invoiceItems, invoices);	
	}
	
	public static void create(InvoiceItem invoiceItem) {
		invoiceItem.invoice = Invoice.findById(invoiceItem.invoice.id);
		invoiceItem.save();
		show("add");
	}
	
	public static void edit(InvoiceItem invoiceItem) {
		invoiceItem.save();
		show("edit");
	}
	
	public static void delete(Long id) {
		if (id != null){
			InvoiceItem invoiceItem = InvoiceItem.findById(id);
			invoiceItem.delete();			
		}
		show("edit");
	}
	
	public static void filter(InvoiceItem invoiceItem) {		
		show("edit");
	}
	
	public static void showNext(String mode, Long id){
		Invoice invoice = Invoice.findById(id);
		List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoice", invoice).fetch();
		if (mode == null || mode.equals(""))
			mode = "edit";
		renderTemplate("InvoiceItems/showNext.html", mode, invoiceItems, invoice);	
		
	}

	public static void createNext(InvoiceItem invoiceItem) {
		invoiceItem.invoice = Invoice.findById(invoiceItem.invoice.id);
		invoiceItem.save();
		showNext("add", invoiceItem.invoice.id);
	}
	
	public static void editNext(InvoiceItem invoiceItem) {
		invoiceItem.save();
		showNext("edit", invoiceItem.invoice.id);
	}
	
	public static void deleteNext(Long id, Long invoiceId) {
		if (id != null){
			InvoiceItem invoiceItem = InvoiceItem.findById(id);
			invoiceItem.delete();		
		}
		showNext("edit", invoiceId);
	}
	
	public static void filterNext(InvoiceItem invoiceItem) {		
		show("edit");
	}
}
