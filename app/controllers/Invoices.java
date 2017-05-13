package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

import models.BusinessPartner;
import models.BusinessYear;
import models.Company;
import models.Invoice;
import models.InvoiceItem;
import models.Item;
import play.mvc.Controller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.time.DateFormatUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;

public class Invoices extends Controller {

	public static void show(String mode){
		List<Invoice> invoices = Invoice.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
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
	
	public static void export(Long id) {
		if (id != null) {
			Invoice invoice = Invoice.findById(id);
			List<InvoiceItem> items = InvoiceItem.find("byInvoice_id", id).fetch();
			String itemValue = "";
			 if (items != null && !items.isEmpty()) {
	        	 for (InvoiceItem item : items) {
	        		 itemValue = Double.toString(item.amount);
	        	 }
			 }
			saveToXML(Integer.toString(invoice.number), 
					  invoice.businessPartner.name, 
					  Integer.toString(invoice.businessYear.year), 
					  invoice.company.name, 
					  DateFormatUtils.format(invoice.dateOfInvoice, "yyyy-MM-dd HH:mm:SS"),
					  DateFormatUtils.format(invoice.dateOfValue, "yyyy-MM-dd HH:mm:SS"),
					  Double.toString(invoice.basis),
					  Double.toString(invoice.tax),
					  Double.toString(invoice.total), 
					  itemValue);
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
	
	public static void saveToXML(String invoiceNumber, 
								 String businessPartnerName, 
								 String businessYearYear, 
								 String companyName,
								 String invoiceDate,
								 String invoiceValueDate,
								 String basic,
								 String tax,
								 String sum,
								 String itemValue) {
		
	  try {
	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.newDocument();
	   
	         Element rootElement = doc.createElement("invoices");
	         doc.appendChild(rootElement);

	         Element invoice_element = doc.createElement("invoice");
	         rootElement.appendChild(invoice_element);
	         
    		 Attr attrItem = doc.createAttribute("amount");
    		 attrItem.setValue(itemValue);
			 invoice_element.setAttributeNode(attrItem);
	         
	         Element company = doc.createElement("company");
	         Attr attr = doc.createAttribute("type");
	         attr.setValue("invoice_company");
	         company.setAttributeNode(attr);
	         company.appendChild(doc.createTextNode(companyName));
	         invoice_element.appendChild(company);

	         Element partner = doc.createElement("partner");
	         Attr attrType = doc.createAttribute("type");
	         attrType.setValue("business_partner");
	         partner.setAttributeNode(attrType);
	         partner.appendChild(doc.createTextNode(businessPartnerName));
	         invoice_element.appendChild(partner);

	         Element year = doc.createElement("year");
	         Attr attrType1 = doc.createAttribute("type");
	         attrType1.setValue("business_year");
	         year.setAttributeNode(attrType1);
	         year.appendChild(doc.createTextNode(businessYearYear));
	         invoice_element.appendChild(year);
	         
	         Element number = doc.createElement("number");
	         Attr attrType3 = doc.createAttribute("type");
	         attrType3.setValue("invoice_number");
	         number.setAttributeNode(attrType3);
	         number.appendChild(doc.createTextNode(invoiceNumber));
	         invoice_element.appendChild(number);
	                  
	         Element dateOfInvoice = doc.createElement("invoiceDate");
	         Attr attrType4 = doc.createAttribute("type");
	         attrType4.setValue("dateOfInvoice");
	         dateOfInvoice.setAttributeNode(attrType4);
	         dateOfInvoice.appendChild(doc.createTextNode(invoiceDate));
	         invoice_element.appendChild(dateOfInvoice);
	         
	         Element dateOfValue = doc.createElement("valueDate");
	         Attr attrType5 = doc.createAttribute("type");
	         attrType5.setValue("dateOfValue");
	         dateOfValue.setAttributeNode(attrType5);
	         dateOfValue.appendChild(doc.createTextNode(invoiceValueDate));
	         invoice_element.appendChild(dateOfValue);
	         
	         Element basicValue = doc.createElement("basicValue");
	         Attr attrType6 = doc.createAttribute("type");
	         attrType6.setValue("basic_number");
	         basicValue.setAttributeNode(attrType6);
	         basicValue.appendChild(doc.createTextNode(basic));
	         invoice_element.appendChild(basicValue);
	         
	         Element taxValue = doc.createElement("taxValue");
	         Attr attrType7 = doc.createAttribute("type");
	         attrType7.setValue("tax_number");
	         taxValue.setAttributeNode(attrType7);
	         taxValue.appendChild(doc.createTextNode(tax));
	         invoice_element.appendChild(taxValue);
	         
	         Element sumValue = doc.createElement("sumValue");
	         Attr attrType8 = doc.createAttribute("type");
	         attrType8.setValue("sum_number");
	         sumValue.setAttributeNode(attrType8);
	         sumValue.appendChild(doc.createTextNode(sum));
	         invoice_element.appendChild(sumValue);
	         
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	         StreamResult result = new StreamResult(new File("invoices.xml"));
	         transformer.transform(source, result);
	               
	         show("edit");
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		
	}
	
	public static void generateKIF(String begin, String end) {
		Date beginDate = new Date();
		Date endDate = new Date();
		if(begin != null && !begin.equals("")) {
			String beginTokens[] = begin.split("-");
			int beginYear = Integer.parseInt(beginTokens[0]);
			int beginMonth = Integer.parseInt(beginTokens[1]);
			int beginDay = Integer.parseInt(beginTokens[2]);
			
			Calendar c = Calendar.getInstance();
	    	c.set(Calendar.HOUR_OF_DAY, 0);
	    	c.set(Calendar.MINUTE, 0);
	    	c.set(Calendar.SECOND, 0);
	    	c.set(Calendar.MILLISECOND, 0);
	    	c.set(Calendar.DAY_OF_MONTH, beginDay);
	    	c.set(Calendar.MONTH, beginMonth-1);
	    	c.set(Calendar.YEAR, beginYear);
	    	beginDate = c.getTime();
	    } else {
	    	beginDate = new Date(Long.MIN_VALUE);
	    }
		if(end != null && !end.equals("")) {
			String endTokens[] = end.split("-");
			int endYear = Integer.parseInt(endTokens[0]);
			int endMonth = Integer.parseInt(endTokens[1]);
			int endDay = Integer.parseInt(endTokens[2]);
			Calendar c = Calendar.getInstance();
	    	c.set(Calendar.HOUR_OF_DAY, 0);
	    	c.set(Calendar.MINUTE, 0);
	    	c.set(Calendar.SECOND, 0);
	    	c.set(Calendar.MILLISECOND, 0);
	    	c.set(Calendar.DAY_OF_MONTH, endDay);
	    	c.set(Calendar.MONTH, endMonth-1);
	    	c.set(Calendar.YEAR, endYear);
	    	endDate = c.getTime();
		} else {
			endDate = new Date(Long.MAX_VALUE);
		}
		System.out.println("****** from "+beginDate+" to "+endDate);
		show("edit");
	}
}
