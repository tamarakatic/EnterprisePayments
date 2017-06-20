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
import models.Permission;
import models.PricelistItem;
import models.User;
import play.Logger;
import play.mvc.Controller;

public class InvoiceItems extends Controller{

	public static void show(String mode){
		if(!Application.authorize("viewInvoiceItems")){
			render("errors/401.html");
		}
		List<InvoiceItem> invoiceItems = InvoiceItem.findAll();
		List<Invoice> invoices = Invoice.findAll();
		List<Item> allArticles = Item.findAll();
		ArrayList<Item> articles = new ArrayList<Item>();
		if(mode != null && mode.equals("filter")) {
			articles.addAll(allArticles);
		} else {
			Date today = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(today);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
	    	c.set(Calendar.SECOND, 0);
	    	c.set(Calendar.MILLISECOND, 0);
	    	today = c.getTime();
			for (Item a : allArticles) {
				if (a.pricelistitem != null) {
					if (!a.pricelistitem.isEmpty()) {
						for(PricelistItem plitem : a.pricelistitem){
							if(!plitem.pricelist.validationDate.after(today)) {
								articles.add(a);
								break;
							}
						}
					}
				}
			}
		}
		if (mode == null || mode.equals(""))
			mode = "edit";
		renderTemplate("InvoiceItems/show.html", mode, invoiceItems, invoices, articles);	
	}
	
	public static void create(InvoiceItem invoiceItem) {
		checkAuthenticity();
		if(!Application.authorize("createInvoiceItem")){
			render("errors/401.html");
		}
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
	    	InvoiceItem i = invoiceItem.save();
	    	Application.logToFile("5_4", i.id, " - invoice_id : "+i.invoice.id+" amount : "+i.amount+" discount : "+i.discount);
	    }
		show("add");
	}
	
	public static void edit(InvoiceItem invoiceItem) {
		checkAuthenticity();
		if(!Application.authorize("editInvoiceItem")){
			render("errors/401.html");
		}
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
	    	
			Application.logToFile("5_5", invoiceItem.id, " - invoice_id : "+invoiceItem.invoice.id+" amount : "
			+invoiceItem.amount+" discount : "+invoiceItem.discount);
	    }
		show("edit");
	}
	
	public static void delete(Long id) {
		if(!Application.authorize("deleteInvoiceItem")){
			render("errors/401.html");
		}
		if (id != null){
			InvoiceItem invoiceItem = InvoiceItem.findById(id);
			Invoice invoice = invoiceItem.invoice;
			invoice.basis -= invoiceItem.basis;
			invoice.tax -= invoiceItem.taxTotal;
			invoice.total -= invoiceItem.total;
			invoice.save();
			invoiceItem.delete();	
			
			Application.logToFile("5_5", id, " - invoice_id : "+invoice.id);
		}
		show("edit");
	}
	
	public static void filter(InvoiceItem invoiceItem) {
		checkAuthenticity();
		List<Invoice> invoices = Invoice.findAll();
		ArrayList<Item> articles = getArticles();
		List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoiceAndAmountAndDiscountAndArticle",
								invoiceItem.invoice,
								invoiceItem.amount,
								invoiceItem.discount,
								invoiceItem.article).fetch();
		renderTemplate("InvoiceItems/show.html", "edit", invoiceItems, invoices, articles);	
	}
	
	public static void showNext(String mode, Long id){
		if(!Application.authorize("viewInvoiceItems")){
			render("errors/401.html");
		}
		Invoice invoice = Invoice.findById(id);
		List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoice", invoice).fetch();
		List<Item> allArticles = Item.findAll();
		ArrayList<Item> articles = new ArrayList<Item>();
		if(mode != null && mode.equals("filter")) {
			articles.addAll(allArticles);
		} else {
			Date today = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(today);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
	    	c.set(Calendar.SECOND, 0);
	    	c.set(Calendar.MILLISECOND, 0);
	    	today = c.getTime();
			for (Item a : allArticles) {
				if (a.pricelistitem != null) {
					if (!a.pricelistitem.isEmpty()) {
						for(PricelistItem plitem : a.pricelistitem){
							if(!plitem.pricelist.validationDate.after(today)) {
								articles.add(a);
								break;
							}
						}
					}
				}
			}
		}
		
		if (mode == null || mode.equals(""))
			mode = "edit";
		renderTemplate("InvoiceItems/showNext.html", mode, invoiceItems, invoice, articles);	
		
	}

	public static void createNext(InvoiceItem invoiceItem) {
		checkAuthenticity();
		if(!Application.authorize("createInvoiceItem")){
			render("errors/401.html");
		}
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
	    	InvoiceItem  i = invoiceItem.save();
	    	Application.logToFile("5_4", i.id, " - invoice_id : "+i.invoice.id+" amount : "+i.amount+" discount : "+i.discount);
	    }
		showNext("add", invoiceItem.invoice.id);
	}
	
	public static void editNext(InvoiceItem invoiceItem) {
		checkAuthenticity();
		if(!Application.authorize("editInvoiceItem")){
			render("errors/401.html");
		}
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
	    	Application.logToFile("5_5", invoiceItem.id, " - invoice_id : "+invoiceItem.invoice.id+" amount : "
	    			+invoiceItem.amount+" discount : "+invoiceItem.discount);
	    }
		showNext("edit", invoiceItem.invoice.id);
	}
	
	public static void deleteNext(Long id, Long invoiceId) {
		if(!Application.authorize("deleteInvoiceItem")){
			render("errors/401.html");
		}
		if (id != null){
			InvoiceItem invoiceItem = InvoiceItem.findById(id);
			Invoice invoice = invoiceItem.invoice;
			invoice.basis -= invoiceItem.basis;
			invoice.tax -= invoiceItem.taxTotal;
			invoice.total -= invoiceItem.total;
			invoice.save();
			invoiceItem.delete();	
			Application.logToFile("5_5", id, " - invoice_id : "+invoice.id);
		}
		showNext("edit", invoiceId);
	}
	
	public static void filterNext(InvoiceItem invoiceItem) {
		checkAuthenticity();
		ArrayList<Item> articles = getArticles();
		List<InvoiceItem> invoiceItems = InvoiceItem.find("byInvoiceAndAmountAndDiscountAndArticle",
				invoiceItem.invoice,
				invoiceItem.amount,
				invoiceItem.discount,
				invoiceItem.article).fetch();
		Invoice invoice = invoiceItem.invoice;
		renderTemplate("InvoiceItems/showNext.html", "edit", invoiceItems, invoice, articles);	
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
		
		invoiceItem.tax = Double.parseDouble(df.format(invoiceItem.tax));
		invoiceItem.taxTotal = Double.parseDouble(df.format(invoiceItem.taxTotal));
		invoiceItem.total = Double.parseDouble(df.format(invoiceItem.total));
		
		Invoice invoice = invoiceItem.invoice;
		invoice.basis += invoiceItem.basis;
		invoice.tax += invoiceItem.taxTotal;
		invoice.total += invoiceItem.total;
		invoice.save();
		return invoiceItem;
		
	}
	
	private static ArrayList<Item> getArticles() {
		List<Item> allArticles = Item.findAll();
		ArrayList<Item> articles = new ArrayList<Item>();

		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		today = c.getTime();
		for (Item a : allArticles) {
			if (a.pricelistitem != null) {
				if (!a.pricelistitem.isEmpty()) {
					for (PricelistItem plitem : a.pricelistitem) {
						if (!plitem.pricelist.validationDate.after(today)) {
							articles.add(a);
							break;
						}
					}
				}
			}
		}
		return articles;	
	}
}
