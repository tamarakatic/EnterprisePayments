package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.sun.beans.finder.FieldFinder;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	
	public String username;
	
	public String password;


}
