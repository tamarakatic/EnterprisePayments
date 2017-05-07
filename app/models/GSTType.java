package models;


import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class GSTType extends Model{
	
	@Column(length = 30, nullable = false)
	public String name;

}
