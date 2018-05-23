package net.thomas.portfolio.shared_objects.hbase_index.model.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.util.UidConverter;

public class UidConverterUnitTest {
	@Test
	public void shouldBeSymmetric() {
		final UidConverter converter = new UidConverter();
		final String uid = "1234567890ABCDEF";
		assertEquals(uid, converter.convert(converter.convert(uid)));
	}
}
