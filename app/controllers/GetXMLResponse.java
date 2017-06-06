package controllers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cryptoXML.XMLUtility;

public abstract class GetXMLResponse {
	
	static String getXMLRequest(InputStream request) {
		String result = "";
   		XMLUtility xmlUtility = new XMLUtility();
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(request));
        StringBuilder sb = new StringBuilder();
        String inline = "";
        try {
			while ((inline = inputReader.readLine()) != null) {
			  sb.append(inline);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			inputReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
              
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();    
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder; 
        Document doc = null;
        try  
        {  
            builder = factory.newDocumentBuilder();  
            InputSource iSource = new InputSource();
            iSource.setCharacterStream(new StringReader(sb.toString()));            
            doc = builder.parse(iSource); 
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        	 
        File schemaFile = new File("invoice_schema.xsd"); 
        byte[] bytes = xmlUtility.documentToByte(doc);
		InputStream inputStream = new ByteArrayInputStream(bytes);
  	    Source xmlFile = new StreamSource(inputStream);
  	    xmlFile.setSystemId("invoices");
  	    
        SchemaFactory schemaFactory = 
        		SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
        	  Schema schema = schemaFactory.newSchema(schemaFile);
        	  Validator validator = schema.newValidator();
        	          	  validator.validate(xmlFile);
        	  NodeList nodeList = doc.getElementsByTagName("invoices_data");
        	  String url = "jdbc:mysql://localhost/eneterprise_payment?useSSL=false&createDatabaseIfNotExist=true";
        	  String user = "root";
        	  String password = "tasha1994";
        	  Connection con = null;
        	  PreparedStatement preparedStatement = null;
        	  for (int i = 0; i < nodeList.getLength(); i++)
        	  {
        		  Node nNode = nodeList.item(i);
        		  
        		  System.out.println("\nCurrent Element :" + nNode.getNodeName());
        		  
        		  if (nNode.getNodeType() == Node.ELEMENT_NODE) 
        		  {
        			  Element element = (Element) nNode; 
        			  result = element.getElementsByTagName("id").item(0).getTextContent(); 
        			  String query = " insert into invoice (company, partner, year, number, "
    			  					+ "dateOfInvoice, dateOfValue, basis, tax, total)"
    			  					+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        			  try {
        				con = DriverManager.getConnection(url, user, password);	
						preparedStatement = con.prepareStatement(query);
						preparedStatement.setString(1, element.getElementsByTagName("company").item(0).getTextContent());
						preparedStatement.setString(2, element.getElementsByTagName("partner").item(0).getTextContent());
						preparedStatement.setString(3, element.getElementsByTagName("year").item(0).getTextContent());
						preparedStatement.setString(4, element.getElementsByTagName("number").item(0).getTextContent());
						preparedStatement.setString(5, element.getElementsByTagName("invoiceDate").item(0).getTextContent());
						preparedStatement.setString(6, element.getElementsByTagName("valueDate").item(0).getTextContent());
						preparedStatement.setString(7, element.getElementsByTagName("basicValue").item(0).getTextContent());
						preparedStatement.setString(8, element.getElementsByTagName("taxValue").item(0).getTextContent());
						preparedStatement.setString(9, element.getElementsByTagName("sumValue").item(0).getTextContent());
						preparedStatement.execute();
						con.close();
					} catch (SQLException e) {					
						System.out.println("Exception with Prepared Statement" + e.getMessage());
					} 	        			  
        		  }
        	  }
        	  System.out.println(xmlFile.getSystemId() + " is valid");
        	} catch (SAXException e) {
        	  System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
        	} catch (IOException e1) {
				e1.printStackTrace();
			}
        return result;
		}
}
