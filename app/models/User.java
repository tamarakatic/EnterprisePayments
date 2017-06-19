package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {

	public String username;

	public String password;
	
	public String email;

	public byte[] salt;

	public User(String username, String password, String email, byte[] salt) {
		this.username = username;
		this.password = password;
		this.salt = salt;
		this.email = email;
	}

}
