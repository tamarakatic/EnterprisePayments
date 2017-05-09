package controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.GSTRate;
import models.GSTType;
import models.Invoice;
import models.InvoiceItem;
import models.Item;
import models.PricelistItem;
import play.mvc.Controller;

public class InvoiceItems extends Controller{

	public static void show(String mode){
		List<InvoiceItem> invoiceItems = InvoiceItem.findAll();
		List<Invoice> invoices = Invoice.findAll();
		List<Item> articles = Item.findAll();
		//izbaciti one koje nemaju bar 1 stavku cenovnika
		if (mode == null || mode.equals(""))
			mode = "edit";
		renderTemplate("InvoiceItems/show.html", mode, invoiceItems, invoices, articles);	
	}
	
	public static void create(InvoiceItem invoiceItem) {
		invoiceItem.invoice = Invoice.findById(invoiceItem.invoice.id);
		invoiceItem.article = Item.findById(invoiceItem.article.id);
		validation.required("invoice", invoiceItem.invoice);
		validation.required("article", invoiceItem.article);
		validation.min("amount", invoiceItem.amount, 0.01);
		if (validation.hasErrors()) {
	        params.flash(); 
	        validation.keep(); 
	    } else {
	    	//Trazenje osnovice po najsvezijem cenovniku za taj proizvod
	    	Item article = invoiceItem.article;
	    	Date date = new Date(0L);
	    	Date today = new Date();
	    	double basis = 0;
	    	
	    	Calendar c = Calendar.getInstance();
	    	c.setTime(today);
	    	c.set(Calendar.HOUR_OF_DAY, 0);
	    	c.set(Calendar.MINUTE, 0);
	    	c.set(Calendar.SECOND, 0);
	    	c.set(Calendar.MILLISECOND, 0);
	    	
	    	for(PricelistItem pli : article.pricelistitem){
	    		if(pli.pricelist.validationDate.after(date)) {
	    			if(c.getTimeInMillis() >= pli.pricelist.validationDate.getTime()) {
	    				date = pli.pricelist.validationDate;
	    				basis = pli.price;
	    			} 
	    		}
	    	}
	    	invoiceItem.price = basis;
	    	
	    	
	    	//Trazenje stope poreza
	    	double taxRate = 0;
	    	date = new Date(0L);
	    	GSTType type = article.articlegroup.gsttype;
	    	for(GSTRate rate : type.gstrate) {
	    		if(rate.date.after(date)) {
	    			if(c.getTimeInMillis() >= rate.date.getTime()) {
	    				date = rate.date;
	    				taxRate = rate.GSTPercent;
	    			}
	    		}
	    	}
	    	invoiceItem.tax = taxRate;
	    	invoiceItem.taxTotal = taxRate/100.0 * basis - basis;
	    	invoiceItem.total = taxRate/100.0 * basis;
	    	invoiceItem.save();
	    }
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
		List<Invoice> invoices = Invoice.findAll();
		List<Item> articles = Item.findAll();
		List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoiceAndAmountAndDiscountAndPriceAndBasisAndTaxAndTaxTotalAndTotal",
								invoiceItem.invoice,
								invoiceItem.amount,
								invoiceItem.discount,
								invoiceItem.price,
								invoiceItem.basis,
								invoiceItem.tax,
								invoiceItem.taxTotal,
								invoiceItem.total).fetch();
		renderTemplate("InvoiceItems/show.html", "edit", invoiceItems, invoices, articles);	
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
		List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoiceAndAmountAndDiscountAndPriceAndBasisAndTaxAndTaxTotalAndTotal",
				invoiceItem.invoice,
				invoiceItem.amount,
				invoiceItem.discount,
				invoiceItem.price,
				invoiceItem.basis,
				invoiceItem.tax,
				invoiceItem.taxTotal,
				invoiceItem.total).fetch();
		Invoice invoice = invoiceItem.invoice;
		renderTemplate("InvoiceItems/showNext.html", "edit", invoiceItems, invoice);	
	}
}
