package controllers;

import java.util.List;

import models.BusinessPartner;
import models.BusinessYear;
import models.Company;
import models.Invoice;
import models.InvoiceItem;
import play.mvc.Controller;

public class Invoices extends Controller {

	public static void show(String mode){
		List<Invoice> invoices = Invoice.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.findAll();
		List<BusinessPartner> businessPartners = BusinessPartner.findAll();
		renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners);	
	}
	
	public static void create(Invoice invoice) {
		Company company = Company.findById(invoice.company.id);
		invoice.company = company;
		invoice.businessYear = BusinessYear.findById(invoice.businessYear.id);
		invoice.businessPartner = BusinessPartner.findById(invoice.businessPartner.id);
		invoice.save();	
		show("add");
	}
	
	public static void edit(Invoice invoice) {
		invoice.save();
		show("edit");
	}
	
	public static void delete(Long id) {
		if (id != null){
			Invoice invoice = Invoice.findById(id);
			invoice.delete();			
		}
		show("edit");
	}
	
	public static void filter(Invoice invoice) {		
		List<Invoice> invoices = Invoice.find("byNumberAndDateOfInvoiceAndDateOfValueAndBasisAndTax", 
												 invoice.number,
												 invoice.dateOfInvoice,
												 invoice.dateOfValue,
												 invoice.basis,
												 invoice.tax).fetch();
		renderTemplate("Invoices/show.html", "edit", invoices);		
	}
	
	public static void nextForm(Long id){
		if(id != null) {
			Invoice invoice = Invoice.findById(id);
			List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoice", invoice).fetch();
			renderTemplate("InvoiceItems/showNext.html", "edit", invoiceItems, invoice);
		}
		show("edit");
	}
}
