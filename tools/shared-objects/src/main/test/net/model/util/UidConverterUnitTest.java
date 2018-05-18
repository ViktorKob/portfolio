package net.model.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UidConverterUnitTest {
	@Test
	public void shouldBeSymmetric() {
		final UidConverter converter = new UidConverter();
		final String uid = "1234567890ABCDEF";
		assertEquals(uid, converter.convert(converter.convert(uid)));
	}
}
