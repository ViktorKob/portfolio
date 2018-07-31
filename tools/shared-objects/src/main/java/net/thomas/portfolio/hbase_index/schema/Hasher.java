package net.thomas.portfolio.hbase_index.schema;

import static java.lang.String.valueOf;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
	private static int counter = 0;
	private MessageDigest hasher;

	public Hasher() {
		try {
			hasher = MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to calculate hash", e);
		}
	}

	public synchronized Hasher addUniqueness() {
		hasher.update(valueOf(counter++).getBytes());
		return this;
	}

	public Hasher add(byte[] value) {
		hasher.update(value);
		return this;
	}

	public String digest() {
		return printHexBinary(hasher.digest());
	}
}