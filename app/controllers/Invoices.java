package controllers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dom4j.io.DocumentSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mysql.jdbc.Statement;
import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;

import cryptoXML.XMLReadCertificateAndSignDoc;
import cryptoXML.XMLUtility;
import groovy.xml.SAXBuilder;
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
import play.db.DB;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Controller;
import sun.misc.BASE64Encoder;

public class Invoices extends Controller {
		
	public static void show(String mode){
		List<Invoice> invoices = Invoice.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		
		List<Company> companies = Company.findAll();
		List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
		List<BusinessPartner> businessPartners = BusinessPartner.find("byKind", "buyer").fetch();
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
			if(invoice.invoiceItems != null && !invoice.invoiceItems.isEmpty()){
				List<Invoice> invoices = Invoice.findAll();
				String mode = "edit";
				boolean hasChildren = true;
				List<Company> companies = Company.findAll();
				List<BusinessYear> businessYears = BusinessYear.find("byActive", true).fetch();
				List<BusinessPartner> businessPartners = BusinessPartner.findAll();
				renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners, hasChildren);	
			} else {
				invoice.delete();			
			} 
		}
		show("edit");
	}
	
	public static void export(Long id) {
		if (id != null) {
			Invoice invoice = Invoice.findById(id);
			List<InvoiceItem> items = InvoiceItem.find("byInvoice_id", id).fetch();
			SaveToXml.saveToXML(id, invoice, items);
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
	
	public static void invoiceReport(Integer id) {
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
			renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners, generatedReport);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		show("edit");
	}
	
	private static String reportName(String name) {
		return Play.applicationPath + File.separator + "app" + File.separator + "reports" + File.separator + name;
	}
		
   	public static void getBody() {
   		String result = GetXMLResponse.getXMLRequest(request.body); 
   		invoiceReport(Integer.parseInt(result));
   		renderText(result);	        
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
			renderTemplate("Invoices/show.html", mode, invoices, companies, businessYears, businessPartners, generatedReport);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		show("edit");
	}
	
}
