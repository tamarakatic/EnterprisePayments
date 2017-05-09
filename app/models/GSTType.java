package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class GSTType extends Model{
	
	@Column(length = 30, nullable = false)
	public String name;
	
	@OneToMany(mappedBy = "gsttype")
	public List<GSTRate> gstrate;
	
	@OneToMany(mappedBy = "gsttype")
	public List<ArticleGroup> articlegroup;

	public GSTType(String name) {
		super();
		this.name = name;
	}
}
