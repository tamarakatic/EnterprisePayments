package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class BusinessPartner extends Model {
	
	@Column(length = 30, nullable = false)
	public String name;
	
	@Column(length = 50)
	public String adress;
	
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

	public BusinessPartner (String name, String adress, String kind, 
							String mobile, String email, String account,
						    Company company) 
	{
		super();
		this.name = name;
		this.adress = adress;
		this.kind = kind;
		this.mobile = mobile;
		this.email = email;
		this.account = account;
		this.company = company;
	}

}
