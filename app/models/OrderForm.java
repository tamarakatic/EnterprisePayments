package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class OrderForm extends Model{
	
	public Date dateOfOrder;
	
	public int numberOfOrder;
	
	@ManyToOne
	public Company company;
	
	@ManyToOne
	public BusinessYear businessYear;
	
	@ManyToOne
	public BusinessPartner businessPartner;
	
	@OneToMany(mappedBy = "orderForm")
	public List<OrderFormItem> orderFormItems;
	
	public OrderForm(Date dateOfOrder, int numberOfOrder) {
		this.dateOfOrder = dateOfOrder;
		this.numberOfOrder = numberOfOrder;
	}
}
