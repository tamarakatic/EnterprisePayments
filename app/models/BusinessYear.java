package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class BusinessYear extends Model {
	
	@Column(nullable = false)
	public int year;
	
	@Column(nullable = false)
	public boolean active;
	
	@ManyToOne
	public Company company;

	@OneToMany(mappedBy = "businessYear")
	public List<Invoice> invoices;
	
	public BusinessYear(int year, boolean active, Company company) 
	{
		super();
		this.year = year;
		this.active = active;
		this.company = company;
	}

}
