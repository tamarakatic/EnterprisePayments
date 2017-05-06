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
		/*List<Invoice> invoices = Invoice.find("byNumberAndDateOfInvoiceAndDateOfValueAndBasisAndTax", 
												 invoice.number,
												 invoice.dateOfInvoice,
												 invoice.dateOfValue,
												 invoice.basis,
												 invoice.tax).fetch();*/
		//renderTemplate("Invoices/show.html", "edit", invoices);	
		show("edit");
	}

}
