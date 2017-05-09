package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class InvoiceItem extends Model{

	public double amount;
	
	public double price;
	
	public double discount;
	
	public double basis;
	
	public double tax;
	
	public double taxTotal;
	
	public double total;
	
	@ManyToOne
	public Invoice invoice;
	
	@ManyToOne
	public Item article;
	
	public InvoiceItem(double amount, double price, double discount, double basis, double tax, double taxTotal,
			double total, Invoice invoice, Item article) {
		super();
		this.amount = amount;
		this.price = price;
		this.discount = discount;
		this.basis = basis;
		this.tax = tax;
		this.taxTotal = taxTotal;
		this.total = total;
		this.invoice = invoice;
		this.article = article;
	}
	
}
