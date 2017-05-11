package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Item extends Model{
	
	@Column (length = 30, nullable = false)
	public String name;
	
	@Column (length = 50)
	public String description;
	
	@ManyToOne
	public ArticleGroup articlegroup;
	
	@OneToMany(mappedBy = "item")
	public List<PricelistItem> pricelistitem;
	
	@OneToMany(mappedBy = "article")
	public List<InvoiceItem> invoiceItems;
	
	@OneToMany(mappedBy = "item")
	public List<OrderFormItem> orderFormItems;

	public Item(String name, String description,ArticleGroup articlegroup) {
		super();
		this.name = name;
		this.description = description;
		this.articlegroup = articlegroup;
	}
}
