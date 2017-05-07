package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

public class Item extends Model{
	
	@Column (nullable = false)
	public Date date;
	
	@OneToMany
	public ArticleGroup articlegroup;

	public Item(Date date, ArticleGroup articlegroup) {
		super();
		this.date = date;
		this.articlegroup = articlegroup;
	}
}
