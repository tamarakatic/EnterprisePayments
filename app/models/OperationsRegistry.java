package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class OperationsRegistry extends Model{
	
	public String code;
	
	public String name;
	
	public OperationsRegistry(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
}
