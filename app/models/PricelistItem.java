package models;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

public class PricelistItem extends Model{
	
	@Column(nullable = false, precision = 2, scale = 9)
	public double price;
	
	@ManyToOne
	public Pricelist pricelist;
	
	@ManyToOne
	public Item item;

	public PricelistItem(double price, Pricelist pricelist, Item item) {
		super();
		this.price = price;
		this.pricelist = pricelist;
		this.item = item;
	}
}
