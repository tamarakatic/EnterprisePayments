package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Role extends Model{

	public String name;
	
	@OneToMany(mappedBy = "role")
	public List<User> users;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "role_permission")
	public List<Permission> permissions;
}
