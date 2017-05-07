package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Pricelist extends Model{
	
	@Column(nullable = false)
	public Date validationDate;

	public Pricelist(Date validationDate) {
		super();
		this.validationDate = validationDate;
	}

}
