package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class BankStatementItemModel extends Model {
	
	@ManyToOne
	public BankStatementModel bankStatement;
	
	public String creditorInfo;
	
	public String creditorAccountNumber;
	
	public String creditorReferenceNumber;
	
	public String creditorModel;
	
	public String originatorInfo;
	
	public String originatorAccountNumber;
	
	public String originatorReferenceNumber;
	
	public String originatorModel;
	
	public String paymentPurpose;
	
	public Date dateOfValue;
	
	public double amount;
	
	public char direction;
	
	public BankStatementItemModel(BankStatementModel bankStatement, String creditorInfo, String creditorAccountNumber,
			String creditorReferenceNumber, String creditorModel, String originatorInfo, String originatorAccountNumber,
			String originatorReferenceNumber, String originatorModel, String paymentPurpose, Date dateOfValue,
			double amount, char direction) {
		super();
		this.bankStatement = bankStatement;
		this.creditorInfo = creditorInfo;
		this.creditorAccountNumber = creditorAccountNumber;
		this.creditorReferenceNumber = creditorReferenceNumber;
		this.creditorModel = creditorModel;
		this.originatorInfo = originatorInfo;
		this.originatorAccountNumber = originatorAccountNumber;
		this.originatorReferenceNumber = originatorReferenceNumber;
		this.originatorModel = originatorModel;
		this.paymentPurpose = paymentPurpose;
		this.dateOfValue = dateOfValue;
		this.amount = amount;
		this.direction = direction;
	}
	
	
}
