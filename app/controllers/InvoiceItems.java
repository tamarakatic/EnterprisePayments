package controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
		List<Item> allArticles = Item.findAll();
		ArrayList<Item> articles = new ArrayList<Item>();
		if(mode != null && mode.equals("filter")) {
			articles.addAll(allArticles);
		} else {
			for (Item a : allArticles) {
				if (a.pricelistitem != null) {
					if (!a.pricelistitem.isEmpty())
						articles.add(a);
				}
			}
		}
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
		validation.min("discount", invoiceItem.discount, 0);
		validation.max("discount", invoiceItem.discount, 100);
		if (validation.hasErrors()) {
	        params.flash(); 
	        validation.keep(); 
	    } else {
	    	invoiceItem = calculate(invoiceItem);
	    	invoiceItem.save();
	    }
		show("add");
	}
	
	public static void edit(InvoiceItem invoiceItem) {
		validation.required("invoice", invoiceItem.invoice);
		validation.required("article", invoiceItem.article);
		validation.min("amount", invoiceItem.amount, 0.01);
		validation.min("discount", invoiceItem.discount, 0);
		validation.max("discount", invoiceItem.discount, 100);
		if (validation.hasErrors()) {
	        params.flash(); 
	        validation.keep(); 
	    } else {
	    	invoiceItem = calculate(invoiceItem);
	    	invoiceItem.save();
	    }
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
		List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoiceAndAmountAndDiscountAndArticle",
								invoiceItem.invoice,
								invoiceItem.amount,
								invoiceItem.discount,
								invoiceItem.article).fetch();
		renderTemplate("InvoiceItems/show.html", "edit", invoiceItems, invoices, articles);	
	}
	
	public static void showNext(String mode, Long id){
		Invoice invoice = Invoice.findById(id);
		List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoice", invoice).fetch();
		List<Item> allArticles = Item.findAll();
		ArrayList<Item> articles = new ArrayList<Item>();
		if(mode != null && mode.equals("filter")) {
			articles.addAll(allArticles);
		} else {
			for (Item a : allArticles) {
				if (a.pricelistitem != null) {
					if (!a.pricelistitem.isEmpty())
						articles.add(a);
				}
			}
		}
		if (mode == null || mode.equals(""))
			mode = "edit";
		renderTemplate("InvoiceItems/showNext.html", mode, invoiceItems, invoice, articles);	
		
	}

	public static void createNext(InvoiceItem invoiceItem) {
		invoiceItem.invoice = Invoice.findById(invoiceItem.invoice.id);
		invoiceItem.article = Item.findById(invoiceItem.article.id);
		validation.required("invoice", invoiceItem.invoice);
		validation.required("article", invoiceItem.article);
		validation.min("amount", invoiceItem.amount, 0.01);
		validation.min("discount", invoiceItem.discount, 0);
		validation.max("discount", invoiceItem.discount, 100);
		if (validation.hasErrors()) {
	        params.flash(); 
	        validation.keep(); 
	    } else {
			invoiceItem = calculate(invoiceItem);
	    	invoiceItem.save();
	    }
		showNext("add", invoiceItem.invoice.id);
	}
	
	public static void editNext(InvoiceItem invoiceItem) {
		validation.required("invoice", invoiceItem.invoice);
		validation.required("article", invoiceItem.article);
		validation.min("amount", invoiceItem.amount, 0.01);
		validation.min("discount", invoiceItem.discount, 0);
		validation.max("discount", invoiceItem.discount, 100);
		if (validation.hasErrors()) {
	        params.flash(); 
	        validation.keep(); 
	    } else {
	    	invoiceItem = calculate(invoiceItem);
	    	invoiceItem.save();
	    }
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
		List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoiceAndAmountAndDiscountAndArticle",
				invoiceItem.invoice,
				invoiceItem.amount,
				invoiceItem.discount,
				invoiceItem.article).fetch();
		Invoice invoice = invoiceItem.invoice;
		renderTemplate("InvoiceItems/showNext.html", "edit", invoiceItems, invoice);	
	}
	
	public static InvoiceItem calculate(InvoiceItem invoiceItem){
		Item article = invoiceItem.article;
    	Date date = new Date(0L);
    	Date today = new Date();
    	double basis = 0;
    	
    	DecimalFormat df = new DecimalFormat("#.##");
    	//double p = Double.parseDouble(df.format(d));
    	
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
    	if(invoiceItem.discount != 0) {
    		invoiceItem.basis = (basis - basis * invoiceItem.discount / 100.0) * invoiceItem.amount;
    	} else {
    		invoiceItem.basis = basis * invoiceItem.amount;
    	}
    	
		double taxRate = 0;
		date = new Date(0L);
		GSTType type = article.articlegroup.gsttype;
		for (GSTRate rate : type.gstrate) {
			if (rate.date.after(date)) {
				if (c.getTimeInMillis() >= rate.date.getTime()) {
					date = rate.date;
					taxRate = rate.GSTPercent;
				}
			}
		}
		
		invoiceItem.basis = Double.parseDouble(df.format(invoiceItem.basis));
		invoiceItem.amount = Double.parseDouble(df.format(invoiceItem.amount));
		invoiceItem.discount = Double.parseDouble(df.format(invoiceItem.discount));
		
		
		invoiceItem.tax = taxRate;
		invoiceItem.taxTotal = taxRate / 100.0 * invoiceItem.basis;
		invoiceItem.total = invoiceItem.basis + invoiceItem.taxTotal;
		
		Invoice invoice = invoiceItem.invoice;
		invoice.basis += invoiceItem.basis;
		invoice.tax += invoiceItem.taxTotal;
		invoice.total += invoiceItem.total;
		invoice.save();
		return invoiceItem;
		
	}
}
