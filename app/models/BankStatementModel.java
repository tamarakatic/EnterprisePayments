package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class BankStatementModel extends Model {
	
	public String accountNumber;
	
	public Date date;
	
	public int sectionNumber;
	
	public double previousBalance;
	
	public int numberOfDeposits;
	
	public double totalDeposited;
	
	public int numberOfWithdrawals;
	
	public double totalWithdrawn;
	
	public double newBalance;
	
	@OneToMany(mappedBy = "bankStatement")
	public List<BankStatementItemModel> bankStatementItems;

	public BankStatementModel(String accountNumber, Date date, int sectionNumber, double previousBalance,
			int numberOfDeposits, double totalDeposited, int numberOfWithdrawals, double totalWithdrawn,
			double newBalance, List<BankStatementItemModel> bankStatementItems) {
		super();
		this.accountNumber = accountNumber;
		this.date = date;
		this.sectionNumber = sectionNumber;
		this.previousBalance = previousBalance;
		this.numberOfDeposits = numberOfDeposits;
		this.totalDeposited = totalDeposited;
		this.numberOfWithdrawals = numberOfWithdrawals;
		this.totalWithdrawn = totalWithdrawn;
		this.newBalance = newBalance;
		this.bankStatementItems = bankStatementItems;
	}
	
	
}
