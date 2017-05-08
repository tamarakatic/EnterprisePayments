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
	
	@Column (nullable = false)
	public Date date;
	
	@ManyToOne
	public ArticleGroup articlegroup;
	
	@OneToMany(mappedBy = "item")
	public List<PricelistItem> pricelistitem;

	public Item(Date date, ArticleGroup articlegroup) {
		super();
		this.date = date;
		this.articlegroup = articlegroup;
	}
}
