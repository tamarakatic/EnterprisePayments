package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import play.db.jpa.Model;

@Entity
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "invoice")
public class Invoice extends Model{

	public Date dateOfInvoice;
	
	public int number;
	
	public Date dateOfValue;
	
	@Column(nullable = false, precision = 2, scale = 9)
	public double basis;
	
	@Column(nullable = false, precision = 2, scale = 9)
	public double tax;
	
	@Column(precision = 2, scale = 9)
	public double total;

	@ManyToOne
	public Company company;
	
	@ManyToOne
	public BusinessYear businessYear;
	
	@ManyToOne
	public BusinessPartner businessPartner;
	
	@OneToMany(mappedBy = "invoice")
	public List<InvoiceItem> invoiceItems;
	
	public Invoice(Date dateOfInvoice, int number, Date dateOfValue, double basis, double tax, double total, Company company,
			BusinessYear businessYear, BusinessPartner businessPartner) {
		super();
		this.dateOfInvoice = dateOfInvoice;
		this.number = number;
		this.dateOfValue = dateOfValue;
		this.basis = basis;
		this.tax = tax;
		this.total = total;
		this.company = company;
		this.businessYear = businessYear;
		this.businessPartner = businessPartner;
	}
	
	
	
}
