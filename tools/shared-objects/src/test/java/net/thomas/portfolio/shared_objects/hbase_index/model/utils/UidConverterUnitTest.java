package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.utils.UidConverter;

public class UidConverterUnitTest {
	private UidConverter converter;

	@Before
	public void setUpForTest() {
		converter = new UidConverter();
	}

	@Test
	public void shouldBeSymmetric() {
		final byte[] uidAsBytes = converter.convert(SOME_UID);
		final String uidAsString = converter.convert(uidAsBytes);
		assertEquals(SOME_UID, uidAsString);
	}

	private static final String SOME_UID = "1234567890ABCDEF";
}