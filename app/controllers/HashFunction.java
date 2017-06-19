package controllers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.plaf.synth.SynthSeparatorUI;

public class HashFunction {

	public static final int ITERATIONS = 10000;
	public static final int HASH_LENGTH = 128;
	public static final int SALT = 32;

	public static byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[SALT];
		sr.nextBytes(salt);
		return salt;
	}

	private static String toHex(byte[] array) throws NoSuchAlgorithmException {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0" + paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}

	public static String hashPassword(String passwordToHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
		char[] chars = passwordToHash.toCharArray();
		byte[] salt = getSalt();

		PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, HASH_LENGTH*2);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = skf.generateSecret(spec).getEncoded();
		return toHex(salt) + toHex(hash);
	}

	public static String hashPassword(String passwordToHash, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		char[] chars = passwordToHash.toCharArray();

		PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, HASH_LENGTH*2);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = skf.generateSecret(spec).getEncoded();
		return toHex(salt) + toHex(hash);
	}

}
