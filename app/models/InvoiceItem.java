package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class InvoiceItem extends Model{

	@Column(precision = 2, scale = 9)
	public Double amount;
	
	@Column(precision = 2, scale = 9)
	public Double price;
	
	@Column(precision = 2, scale = 3)
	public Double discount;
	
	@Column(precision = 2, scale = 9)
	public Double basis;
	
	@Column(precision = 2, scale = 3)
	public Double tax;
	
	@Column(precision = 2, scale = 9)
	public Double taxTotal;
	
	@Column(precision = 2, scale = 9)
	public Double total;
	
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
