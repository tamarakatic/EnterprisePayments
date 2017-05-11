package controllers;

import java.util.ArrayList;
import java.util.List;

import models.BusinessPartner;
import models.BusinessYear;
import models.Company;
import models.Invoice;
import models.InvoiceItem;
import models.Item;
import play.mvc.Controller;

public class Invoices extends Controller {

	public static void show(String mode){
		List<Invoice> invoices = Invoice.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
		//List<BusinessPartner> businessPartners = BusinessPartner.find("byKind", "buyer").fetch();
		List<BusinessPartner> businessPartners = BusinessPartner.findAll();
		renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners);	
	}
	
	public static void create(Invoice invoice) {
		Company company = Company.findById(invoice.company.id);
		invoice.company = company;
		invoice.businessYear = BusinessYear.findById(invoice.businessYear.id);
		invoice.businessPartner = BusinessPartner.findById(invoice.businessPartner.id);
				
		validation.required("company",invoice.company);
		validation.required("business partner", invoice.businessPartner);
		validation.required("business year",invoice.businessYear);
		validation.required("date of invoice",invoice.dateOfInvoice);
		validation.required("date of value",invoice.dateOfValue);
		
		if(validation.hasErrors()) {
	          params.flash(); 
	          validation.keep(); 
	    } else {
	    	int num = 0;
			List<Invoice> invoicesInYear = Invoice.find("byBusinessYear", invoice.businessYear).fetch();
			for(Invoice inv : invoicesInYear) {
				if(inv.number > num) {
					num = inv.number;
				}
			}
			invoice.number = ++num;
			invoice.save();
	    }
		show("add");
	}
	
	public static void edit(Invoice invoice) {
		validation.required("company",invoice.company);
		validation.required("business partner", invoice.businessPartner);
		validation.required("business year",invoice.businessYear);
		validation.min("number", invoice.number, 1);
		validation.required("date of invoice",invoice.dateOfInvoice);
		validation.required("date of value",invoice.dateOfValue);
		
		if(validation.hasErrors()) {
	          params.flash(); 
	          validation.keep(); 
	    } else {
	    	  invoice.save();
	    }
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
		List<Invoice> invoices = Invoice.find("byDateOfInvoiceAndDateOfValueAndCompanyAndBusinessPartnerAndBusinessYear", 
												 invoice.dateOfInvoice,
												 invoice.dateOfValue,
												 invoice.company,
												 invoice.businessPartner,
												 invoice.businessYear).fetch();
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.findAll();
		List<BusinessPartner> businessPartners = BusinessPartner.findAll();
		renderTemplate("Invoices/show.html", "edit", invoices, companies, businessYears, businessPartners);		
	}
	
	public static void nextForm(Long id){
		if(id != null) {
			InvoiceItems.showNext("edit", id);
		}
		show("edit");
	}
}
