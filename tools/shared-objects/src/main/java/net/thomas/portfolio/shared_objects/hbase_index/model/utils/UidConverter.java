package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import javax.xml.bind.DatatypeConverter;

public class UidConverter {
	public String convert(byte[] uid) {
		return DatatypeConverter.printHexBinary(uid);
	}

	public byte[] convert(String uid) {
		return DatatypeConverter.parseHexBinary(uid);
	}
}
