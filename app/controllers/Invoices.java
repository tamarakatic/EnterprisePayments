package controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
>>>>>>> 4f76e62bff28ce8edca8a41b4cf206206a694014
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.time.DateFormatUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.example.service.paymentorder.PaymentOrder;
import com.example.service.paymentorder.TCompanyData;

import models.BusinessPartner;
import models.BusinessYear;
import models.Company;
import models.Invoice;
import models.InvoiceItem;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import play.Logger;
import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Controller;
import soap.CompanyService;
import soap.CompanyServiceImplService;

public class Invoices extends Controller {

	public static void show(String mode){
		if(!Application.authorize("viewInvoices")){
			render("errors/401.html");
		}
		List<Invoice> invoices = Invoice.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
		List<BusinessPartner> businessPartners = BusinessPartner.find("byKind", "buyer").fetch();
		renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners);	
	}
	
	public static void create(Invoice invoice) {
		if(!Application.authorize("createInvoice")){
			render("errors/401.html");
		}
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
			Application.logToFile("5_1", invoice.id, " - account : "+invoice.businessPartner.account);
	    }
		show("add");
	}
	
	public static void edit(Invoice invoice) {
		if(!Application.authorize("editInvoice")){
			render("errors/401.html");
		}
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
			  Application.logToFile("5_2", invoice.id, " - account : " + invoice.businessPartner.account 
					  + " total : "+invoice.total);
	    }
		show("edit");
	}
	

	public static void delete(Long id) throws IOException, ParserConfigurationException, TransformerException {
		
		if(!Application.authorize("deleteInvoice")){
			render("errors/401.html");
		}
		if (id != null){
			Invoice invoice = Invoice.findById(id);
			if(invoice.invoiceItems != null && !invoice.invoiceItems.isEmpty()){
				List<Invoice> invoices = Invoice.findAll();
				String mode = "edit";
				boolean hasChildren = true;
				List<Company> companies = Company.findAll();
				List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
				List<BusinessPartner> businessPartners = BusinessPartner.findAll();
				
				String code = "5_3";
				String user = Security.connected();
				Logger.error(code + " : user = "+user + " id = "+invoice.id);
				
				renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners, hasChildren);	
			} else {
				invoice.delete();	
				Application.logToFile("5_3", invoice.id, " - account: "+invoice.businessPartner.account + " total: "+ invoice.total);
			} 
		}
		show("edit");
	}
	
	public static void export(Long id) {
		if(!Application.authorize("exportInvoiceAsXML")){
			render("errors/401.html");
		}
		if (id != null) {
			Invoice invoice = Invoice.findById(id);
			List<InvoiceItem> items = InvoiceItem.find("byInvoice_id", id).fetch();
			
			saveToXML(invoice, items);
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
		List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
		List<BusinessPartner> businessPartners = BusinessPartner.find("byKind", "buyer").fetch();
		renderTemplate("Invoices/show.html", "edit", invoices, companies, businessYears, businessPartners);		
	}
	
	public static void nextForm(Long id){
		if(id != null) {
			InvoiceItems.showNext("edit", id);
		}
		show("edit");
	}
	
	public static void invoiceReport(Integer id) throws IOException {
		if(!Application.authorize("exportInvoiceAsPdf")){
			render("errors/401.html");
		}
		Long idd = Long.parseLong(id.toString());
		Invoice inv = Invoice.findById(idd);
		if(inv.invoiceItems == null || inv.invoiceItems.size() ==0){
			List<Invoice> invoices = Invoice.findAll();
			String mode = "edit";
			String errorReport = "errorReport";
			List<Company> companies = Company.findAll();
			List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
			List<BusinessPartner> businessPartners = BusinessPartner.find("byKind", "buyer").fetch();
			renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners, errorReport);
			
		}
		try {
			Map reportParams = new HashMap();
			reportParams.put("invoicesjasper", id);

		    String compiledFile = "app/reports/" + "Blank_A4" + ".jasper";
		    JasperCompileManager.compileReportToFile("app/reports/" + "Blank_A4" + ".jrxml", compiledFile);
		    JasperPrint jrprint = JasperFillManager.fillReport(compiledFile, reportParams, play.db.DB.getConnection());
			JasperExportManager.exportReportToPdfFile(jrprint, reportName("report-" + id + ".pdf"));
			
			List<Invoice> invoices = Invoice.findAll();
			String mode = "edit";
			String generatedReport = "generatedReport";
			List<Company> companies = Company.findAll();
			List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
			List<BusinessPartner> businessPartners = BusinessPartner.find("byKind", "buyer").fetch();
			
			Application.logToFile("5_8", idd, "");
			
			renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners, generatedReport);
		} catch (Exception e) {
			Application.logErrorToFile("5_8", idd);
			e.printStackTrace();
		}
		
		show("edit");
	}
	
	private static String reportName(String name) {
		return Play.applicationPath + File.separator + "app" + File.separator + "reports" + File.separator + name;
	}
	
	private static void saveToXML(Invoice invoice, List<InvoiceItem> items) {
		String invoiceNumber= Integer.toString(invoice.number);
		String bussinessPartner = invoice.businessPartner.name;
		String businessYear = Integer.toString(invoice.businessYear.year);
	    String companyName = invoice.company.name;
	    String invoiceDate = DateFormatUtils.format(invoice.dateOfInvoice, "yyyy-MM-dd HH:mm:SS");
	    String invoiceValueDate = DateFormatUtils.format(invoice.dateOfValue, "yyyy-MM-dd HH:mm:SS");
	    String basis = Double.toString(invoice.basis);
	    String tax = Double.toString(invoice.tax);
	    String total = Double.toString(invoice.total);

	  try {
	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.newDocument();
	   
	         Element topElement = doc.createElement("invoices");
	         doc.appendChild(topElement);

	         Element rootElement = doc.createElement("invoices_data");
	         topElement.appendChild(rootElement);

	         Element company = doc.createElement("company");
	         Attr attr = doc.createAttribute("type");
	         attr.setValue("invoice_company");
	         company.setAttributeNode(attr);
	         company.appendChild(doc.createTextNode(companyName));
	         rootElement.appendChild(company);

	         Element partner = doc.createElement("partner");
	         Attr attrType = doc.createAttribute("type");
	         attrType.setValue("business_partner");
	         partner.setAttributeNode(attrType);
	         partner.appendChild(doc.createTextNode(bussinessPartner));
	         rootElement.appendChild(partner);

	         Element year = doc.createElement("year");
	         Attr attrType1 = doc.createAttribute("type");
	         attrType1.setValue("business_year");
	         year.setAttributeNode(attrType1);
	         year.appendChild(doc.createTextNode(businessYear));
	         rootElement.appendChild(year);
	         
	         Element number = doc.createElement("number");
	         Attr attrType3 = doc.createAttribute("type");
	         attrType3.setValue("invoice_number");
	         number.setAttributeNode(attrType3);
	         number.appendChild(doc.createTextNode(invoiceNumber));
	         rootElement.appendChild(number);
	                  
	         Element dateOfInvoice = doc.createElement("invoiceDate");
	         Attr attrType4 = doc.createAttribute("type");
	         attrType4.setValue("dateOfInvoice");
	         dateOfInvoice.setAttributeNode(attrType4);
	         dateOfInvoice.appendChild(doc.createTextNode(invoiceDate));
	         rootElement.appendChild(dateOfInvoice);
	         
	         Element dateOfValue = doc.createElement("valueDate");
	         Attr attrType5 = doc.createAttribute("type");
	         attrType5.setValue("dateOfValue");
	         dateOfValue.setAttributeNode(attrType5);
	         dateOfValue.appendChild(doc.createTextNode(invoiceValueDate));
	         rootElement.appendChild(dateOfValue);
	         
	         Element basicValue = doc.createElement("basicValue");
	         Attr attrType6 = doc.createAttribute("type");
	         attrType6.setValue("basic_number");
	         basicValue.setAttributeNode(attrType6);
	         basicValue.appendChild(doc.createTextNode(basis));
	         rootElement.appendChild(basicValue);
	         
	         Element taxValue = doc.createElement("taxValue");
	         Attr attrType7 = doc.createAttribute("type");
	         attrType7.setValue("tax_number");
	         taxValue.setAttributeNode(attrType7);
	         taxValue.appendChild(doc.createTextNode(tax));
	         rootElement.appendChild(taxValue);
	         
	         Element sumValue = doc.createElement("sumValue");
	         Attr attrType8 = doc.createAttribute("type");
	         attrType8.setValue("sum_number");
	         sumValue.setAttributeNode(attrType8);
	         sumValue.appendChild(doc.createTextNode(total));
	         rootElement.appendChild(sumValue);

	         if (items != null) {
				 for (InvoiceItem item : items) {
					 Element invoice_element =  doc.createElement("invoice");
					 Attr attrItem = doc.createAttribute("amount");
					 Attr attrItemTotal = doc.createAttribute("total");
					 Attr attrItemPrice = doc.createAttribute("price");
					 attrItem.setValue(Double.toString(item.amount));
					 attrItemTotal.setValue(Double.toString(item.total));
					 attrItemPrice.setValue(Double.toString(item.price));
					 invoice_element.setAttributeNode(attrItem);
					 invoice_element.setAttributeNode(attrItemTotal);
					 invoice_element.setAttributeNode(attrItemPrice);
					 topElement.appendChild(invoice_element);
				 }
	         }
	         
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	         StreamResult result = new StreamResult(new File("invoices.xml"));
	         transformer.transform(source, result);
	               
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		}
	
	public static void generateKIF(String begin, String end) {
		if(!Application.authorize("exportMultipleInvoicesAsPdf")){
			render("errors/401.html");
		}
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
	    	Calendar c = Calendar.getInstance();
			c.setTime(beginDate);
			c.set(Calendar.YEAR, 1990);
			beginDate = c.getTime();
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
		}
		try {
			Map reportParams = new HashMap();
			reportParams.put("beginDate", beginDate);
			reportParams.put("endDate", endDate);

		    String compiledFile = "app/reports/" + "KIF" + ".jasper";
		    JasperCompileManager.compileReportToFile("app/reports/" + "KIF" + ".jrxml", compiledFile);
		    JasperPrint jrprint = JasperFillManager.fillReport(compiledFile, reportParams, play.db.DB.getConnection());
			JasperExportManager.exportReportToPdfFile(jrprint, reportName("report-" + "KIF" + ".pdf"));
			
			List<Invoice> invoices = Invoice.findAll();
			String mode = "edit";
			String generatedReport = "generatedReport";
			List<Company> companies = Company.findAll();
			List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
			List<BusinessPartner> businessPartners = BusinessPartner.find("byKind", "buyer").fetch();
			
			Application.logToFile("5_9", 0L, " - from : "+begin + "; to : "+end);
			
			renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners, generatedReport);	
			
		} catch (Exception e) {
			Application.logErrorToFile("5_9", 0L);
			e.printStackTrace();
		}
		show("edit");
	}
	
	public static void send() throws ParserConfigurationException, TransformerException {
		HttpResponse httpResponse = null;
		String username = "";
		String password = "";
		String url = "http://localhost:8080/invoices/show";
		String postBody = "";

	//	BankStatementRequest b = new BankStatementRequest("111", new Date(), 1);
		
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
                "<Emp id=\"1\"><name>Pankaj</name><age>25</age>\n"+
                "<role>Developer</role><gen>Male</gen></Emp>";
		
		try {
		    httpResponse = WS.url(url)
		        .setHeader("Content-Type", "text/xml; charset=UTF-8")
		        .setHeader("SOAPAction", "")
		        .body(xmlStr).post();

		    Document document = httpResponse.getXml();
		 //   String value = XPath.selectText("//value", document);
		 //   Node node = XPath.selectNode("//node", document);

		    // Do things with the nodes, value and so on

		} catch (Exception e) {
		    Logger.error("Do something with the connection error: %s", e);
		}
	}

	
	public static void createPaymentOrder(Long invoiceId, BigDecimal amount, boolean isUrgent, String currency) throws DatatypeConfigurationException{
		if (invoiceId != null) {
			Invoice invoice = Invoice.findById(invoiceId);
			Company company = invoice.company;
			BusinessPartner bp = invoice.businessPartner;
			
			TCompanyData debtorData = new TCompanyData();
			debtorData.setInfo(company.name + " " + company.address);
			//debtorData.setAccountNumber(company.accountNumber);
			//debtorData.setModel(model);
			//debtorData.setReferenceNumber(company.referenceNumber);
			
			TCompanyData creditorData = new TCompanyData();
			creditorData.setInfo(bp.name + " " + bp.address);
			creditorData.setAccountNumber(bp.account);
			//creditorData.setModel(model);
			//creditorData.setReferenceNumber(bp.referenceNumber);
			
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			XMLGregorianCalendar dateOfPaymentXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			
			calendar.setTime(invoice.dateOfValue);
			XMLGregorianCalendar dateOfValueXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			
			PaymentOrder po = new PaymentOrder();
			po.setMessageId("messageId");
			po.setCreditor(creditorData);
			po.setDebtor(debtorData);
			po.setPaymentPurpose("Payment based on a recieved invoice");
			po.setUrgent(isUrgent);
			po.setAmount(amount);
			po.setCurrency(currency);
			po.setDateOfPayment(dateOfPaymentXML);
			po.setDateOfValue(dateOfValueXML);
			
			
			CompanyServiceImplService service = new CompanyServiceImplService();
			CompanyService companyService = service.getCompanyServiceImplPort();
			String response = companyService.processPaymentOrder(po);
			System.out.println(response);
		}
		show("edit");
	}

}
