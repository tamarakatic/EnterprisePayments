package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	
	public String username;
	
	public String password;
	
	@ManyToOne
	public Role role;

}
