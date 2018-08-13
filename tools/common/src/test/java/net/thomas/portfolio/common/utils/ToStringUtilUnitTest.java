package net.thomas.portfolio.common.utils;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;

public class ToStringUtilUnitTest {
	@Test
	public void shouldContainValuesInToString() throws IllegalArgumentException, IllegalAccessException {
		final SomeObject object = new SomeObject();
		final String asString = asString(object);
		for (final Field field : SomeObject.class.getDeclaredFields()) {
			assertTrue(asString.contains(field.getName() + "=" + field.get(object)));
		}
	}

	public static class SomeObject {
		public boolean toggleValue = true;
		public int smallNumber = 1;
		public long largeNumber = Long.MAX_VALUE;
		public int negativeNumber = -1;
		public String someString = "someString";
	}
}
