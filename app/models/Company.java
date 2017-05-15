package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Company extends Model {
	
	@Column(length = 30, nullable = false)
	public String name;
	
	@Column(length = 8, nullable = false)
	public int PIB;
	
	@Column(length = 50)
	public String address;
	
	@Column(length = 30)
	public String mobile;
	
	@Column(length = 8, nullable = false)
	public String MBR;

	@OneToMany(mappedBy = "company")
	public List<BusinessYear> businessYear;
	
	@OneToMany(mappedBy = "company")
	public List<BusinessPartner> businessPartner;
	
	@OneToMany(mappedBy = "company")
	public List<Invoice> invoices;
	
	@OneToMany(mappedBy = "company")
	public List<OrderForm> orderForms;
	
	public Company(String naziv, int pib, String adresa, 
				   String mobile, String jmbg) 
	{
		super();
		this.name = naziv;
		PIB = pib;
		this.address = adresa;
		this.mobile = mobile;
		MBR = jmbg;
	}
		
}
