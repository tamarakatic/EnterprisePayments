package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class BusinessYear extends Model {
	
	@Column(nullable = false)
	public int year;
	
	@Column(nullable = false)
	public boolean active;
	
	@ManyToOne
	public Company company;

	public BusinessYear(int year, boolean active, Company company) 
	{
		super();
		this.year = year;
		this.active = active;
		this.company = company;
	}

}
