package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class ArticleGroup extends Model{
	
	@Column(length = 30, nullable = false)
	public String name;
	
	@ManyToOne
	public GSTType gsttype;
	
	@OneToMany(mappedBy = "articlegroup")
	public List<Item> item;

	public ArticleGroup(String name, GSTType gsttype) {
		super();
		this.name = name;
		this.gsttype = gsttype;
	}
}
