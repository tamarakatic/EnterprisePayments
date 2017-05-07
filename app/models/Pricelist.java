package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Pricelist extends Model{
	
	@Column(nullable = false)
	public Date validationDate;
	
	@OneToMany(mappedBy = "pricelist")
	public List<PricelistItem> pricelistItem;

	public Pricelist(Date validationDate) {
		super();
		this.validationDate = validationDate;
	}

}
