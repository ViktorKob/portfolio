package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class UidConverter {
	public String convert(byte[] uid) {
		return printHexBinary(uid);
	}

	public byte[] convert(String uid) {
		return parseHexBinary(uid);
	}
}
