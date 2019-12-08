package net.thomas.portfolio.hbase_index.schema.util;

import static java.lang.String.valueOf;
import static java.security.MessageDigest.getInstance;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.thomas.portfolio.common.utils.ProgrammingException;

public class Hasher {
	private static int counter = 0;
	private MessageDigest hasher;

	public Hasher() {
		try {
			hasher = getInstance("MD5");
		} catch (final NoSuchAlgorithmException cause) {
			throw new ProgrammingException("MD5 hasher is no longer available", cause);
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