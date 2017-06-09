package controllers;

import java.io.File;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.SecretKey;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.time.DateFormatUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mysql.fabric.Response;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.RequestBuilder;

import cryptoXML.XMLReadCertificateAndSignDoc;
import cryptoXML.XMLUtility;
import models.Invoice;
import models.InvoiceItem;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import sun.misc.BASE64Encoder;

public abstract class SaveToXml {
	
	static void saveToXML(Long id, Invoice invoice, List<InvoiceItem> items) {
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
	                  
	         Element invoicesId = doc.createElement("id");
	         invoicesId.appendChild(doc.createTextNode(id.toString()));
	         
	         Element company = doc.createElement("company");
	         company.appendChild(doc.createTextNode(companyName));
	         
	         Element partner = doc.createElement("partner");
	         partner.appendChild(doc.createTextNode(bussinessPartner));
	         
	         Element year = doc.createElement("year");
	         year.appendChild(doc.createTextNode(businessYear));
	         
	         Element number = doc.createElement("number");
	         number.appendChild(doc.createTextNode(invoiceNumber));
	         
	         Element dateOfInvoice = doc.createElement("invoiceDate");
	         dateOfInvoice.appendChild(doc.createTextNode(invoiceDate));
	         
	         Element dateOfValue = doc.createElement("valueDate");
	         dateOfValue.appendChild(doc.createTextNode(invoiceValueDate));
	         
	         Element basicValue = doc.createElement("basicValue");
	         basicValue.appendChild(doc.createTextNode(basis));
	         
	         Element taxValue = doc.createElement("taxValue");
	         taxValue.appendChild(doc.createTextNode(tax));
	         
	         Element sumValue = doc.createElement("sumValue");
	         sumValue.appendChild(doc.createTextNode(total));
	         
	         rootElement.appendChild(invoicesId);
	         rootElement.appendChild(company);
	         rootElement.appendChild(partner);
	         rootElement.appendChild(year);
	         rootElement.appendChild(number);
	         rootElement.appendChild(dateOfInvoice);
	         rootElement.appendChild(dateOfValue);
	         rootElement.appendChild(basicValue);
	         rootElement.appendChild(taxValue);
	         rootElement.appendChild(sumValue);
	         
	         if (items != null) {
				 for (InvoiceItem item : items) {
					 Element invoice_element =  doc.createElement("invoice");
					 Element invoice_amount =  doc.createElement("amount");
					 invoice_amount.appendChild(doc.createTextNode(Double.toString(item.amount)));
					 Element invoice_total =  doc.createElement("total");
					 invoice_total.appendChild(doc.createTextNode(Double.toString(item.total)));
					 Element invoice_price =  doc.createElement("price");
					 invoice_price.appendChild(doc.createTextNode(Double.toString(item.price)));
					 invoice_element.appendChild(invoice_amount);
					 invoice_element.appendChild(invoice_total);
					 invoice_element.appendChild(invoice_price);
					 topElement.appendChild(invoice_element);
				 }
	         }
	         	         
	         XMLReadCertificateAndSignDoc xml = new XMLReadCertificateAndSignDoc();
	         XMLUtility xmlUtility = new XMLUtility();
	 		 String keyStoreFile = "./data/keystores/primer.jks";
	 		  		
	 		 SecretKey secretKey = xmlUtility.generateDataEncryptionKey();
	 		 System.out.println(new BASE64Encoder().encode((secretKey.getEncoded())));
	 		
	 		 Certificate cert = xml.readCertificate(keyStoreFile, "primer", "primer");
	 		 doc = xmlUtility.encrypt(doc, secretKey, cert);
	 		
	 		 PrivateKey privateKey = xmlUtility.readPrivateKey(keyStoreFile, "primer", "primer", "primer");
	 		 doc = xml.signDocument(doc, privateKey, cert);
	 		  		
	 		 System.out.println("\n Firma A salje XML dokumenat firmi B ");	
	 		 
	 	     System.out.println("\n Proverava se validacija digitalnog potpisa...");	 	     

	 	     boolean res = xmlUtility.verifySignature(doc);
			 if(res) {
				System.out.println("\n Potpis je validan, dokument se desifruje ");
				doc = xmlUtility.decrypt(doc, privateKey);
				System.out.println("\n Desifrovanje zavrseno, firma B je primila dokument ");					
			 
			 } else {
				System.out.println("\n Potpis nije validan! Dokument se odbacuje! ");
			 }

	 		  		  		
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	         StringWriter writer = new StringWriter();
	         StreamResult result = new StreamResult(writer);     

	         transformer.transform(source, result);	   

	         HttpResponse response = WS.url("https://localhost:9444/invoices")
	        		 .setHeader("Content-Type", "application/xml")
	        		 .setHeader("PrivateKey", privateKey.toString())
	        		 .body(writer.toString())
	        		 .post();
	
            System.out.println("Responseeeeeeee: " + response.getString());
     
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	}

}
