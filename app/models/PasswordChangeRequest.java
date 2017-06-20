package models;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class PasswordChangeRequest extends Model {
	
	@Column
	public long time;
	
	@Column
	public String username;
	
	@Column
	public boolean isUsed;
	
	@Column
	public String token;
	
	public PasswordChangeRequest(String username) {
		this.username = username;
		time = System.currentTimeMillis();
		isUsed = false;
		try {
			token = generateToken();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private String generateToken() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] token = new byte[32];
		sr.nextBytes(token);
		
		BigInteger bi = new BigInteger(1, token);
		String hex = bi.toString(16);
		int paddingLength = (token.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0" + paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}

}
