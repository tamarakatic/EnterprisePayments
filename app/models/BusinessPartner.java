package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class BusinessPartner extends Model {
	
	@Column(length = 30, nullable = false)
	public String name;
	
	@Column(length = 50)
	public String address;
	
	@Column(length = 10, nullable = false)
	public String kind;
	
	@Column(length = 30)
	public String mobile;
	
	@Column(length = 30)
	public String email;
	
	@Column(length = 30, nullable = false)
	public String account;
	
	@ManyToOne
	public Company company;
	
	@OneToMany(mappedBy = "businessPartner")
	public List<Invoice> invoices;
	
	@OneToMany(mappedBy = "businessPartner")
	public List<OrderForm> orderForms;

	public BusinessPartner (String name, String address, String kind, 
							String mobile, String email, String account,
						    Company company) 
	{
		super();
		this.name = name;
		this.address = address;
		this.kind = kind;
		this.mobile = mobile;
		this.email = email;
		this.account = account;
		this.company = company;
	}

}
