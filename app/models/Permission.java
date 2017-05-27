package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;

@Entity
public class Permission extends Model{
	
	public String name;
	
	public Permission(String name) {
		super();
		this.name = name;
	}
	
}
