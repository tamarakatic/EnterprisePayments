package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class User extends Model {

	public String username;

	public String password;
	
	public String email;

	public byte[] salt;
	
	@ManyToOne
	public Role role;

	public User(String username, String password, String email, byte[] salt, Role role) {
		this.username = username;
		this.password = password;
		this.salt = salt;
		this.email = email;
		this.role = role;
	}
}
