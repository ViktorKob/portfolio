package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import static java.util.Calendar.NOVEMBER;
import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;

public class DateConverterUnitTest {
	private DateConverter converter;

	@Before
	public void setup() {
		converter = new DateConverter.Iec8601DateConverter();
	}

	@Test
	public void timestampConversionShouldBeReversible() {
		final GregorianCalendar calendar = new GregorianCalendar(2017, NOVEMBER, 22, 11, 15, 53);
		final long expectedTimestamp = calendar.getTimeInMillis();
		final String dateAsString = converter.formatTimestamp(expectedTimestamp);
		final long actualTimestamp = converter.parseTimestamp(dateAsString);
		assertEquals(expectedTimestamp, actualTimestamp);
	}

	@Test
	public void timestampAsDateConversionShouldBeReversible() {
		final GregorianCalendar calendar = new GregorianCalendar(2017, 10, 22);
		final long expectedTimestamp = calendar.getTimeInMillis();
		final String dateAsString = converter.formatDateTimestamp(expectedTimestamp);
		final long actualTimestamp = converter.parseTimestamp(dateAsString);
		assertEquals(expectedTimestamp, actualTimestamp);
	}
}