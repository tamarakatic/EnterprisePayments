package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class OrderFormItem extends Model{
	
	public double amount;
	
	@ManyToOne
	public OrderForm orderForm;
	
	@ManyToOne
	public Item item;
	
	public OrderFormItem(double amout) {
		this.amount = amout;
	}
}
