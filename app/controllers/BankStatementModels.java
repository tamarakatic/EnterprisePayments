package controllers;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.example.service.bankstatement.BankStatement;
import com.example.service.bankstatement.BankStatement.BankStatementItem;
import com.example.service.bankstatementrequest.BankStatementRequest;

import models.BankStatementModel;
import play.mvc.Controller;
import soap.CompanyService;
import soap.CompanyServiceImplService;

public class BankStatementModels extends Controller {

	public static void show() {
		List<BankStatementModel> bankStatements = BankStatementModel.findAll();
		renderTemplate("BankStatementModels/show.html", bankStatements);
	}
	
	public static void filter(BankStatementModel bankStatement) {
		System.out.println("***** filter ******");
		show();
	}
	
	public static void requestBankStatement(String accountNumber, String javascriptDate) throws MalformedURLException, DatatypeConfigurationException {
		if (javascriptDate != null && !javascriptDate.equals("")) {
			String tokens[] = javascriptDate.split("-");
			int year = Integer.parseInt(tokens[0]);
			int month = Integer.parseInt(tokens[1]);
			int day = Integer.parseInt(tokens[2]);

			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.DAY_OF_MONTH, day);
			c.set(Calendar.MONTH, month - 1);
			c.set(Calendar.YEAR, year);
			Date date = c.getTime();
			
			int sectionNumberInt = 1;
			
			CompanyServiceImplService service = new CompanyServiceImplService();
			CompanyService companyService = service.getCompanyServiceImplPort();
		
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			XMLGregorianCalendar dateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			
			BankStatementRequest bsr = new BankStatementRequest();
			bsr.setAccountNumber(accountNumber);
			bsr.setDate(dateXML);
			bsr.setSectionNumber(sectionNumberInt);
			BankStatement bankStatement = companyService.processBankStatementRequest(bsr);
			System.out.println(bankStatement.getAccountNumber());
			for(BankStatementItem bsi : bankStatement.getBankStatementItem()){
				System.out.println(bsi.getPaymentPurpose());
			}
			//for petlja za preseke dok ne bude prazan presek, svaki presek jedan soap poziv
		}
		show();
	}
}
