package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import static java.util.Calendar.NOVEMBER;
import static java.util.TimeZone.getTimeZone;
import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

public class DateConverterUnitTest {
	private static final String FORMATTED_REFERENCE_DATE = "2017-11-22";
	private static final String FORMATTED_REFERENCE_DATE_TIME = "2017-11-22T11:15:53";
	private static final String FORMATTED_REFERENCE_DATE_WITH_TIME_ZONE = "2017-11-22+00:00";
	private static final String FORMATTED_REFERENCE_DATE_TIME_WITH_TIME_ZONE = "2017-11-22T11:15:53+00:00";
	private static final String FORMATTED_REFERENCE_DATE_TIME_WITH_ANOTHER_TIME_ZONE = "2017-11-22T12:15:53+01:00";
	private static final long REFERENCE_DATE_UTC_TIMESTAMP;
	private static final long REFERENCE_DATE_TIME_UTC_TIMESTAMP;

	static {
		final GregorianCalendar calendar = new GregorianCalendar(getTimeZone("UTC"));
		calendar.set(2017, NOVEMBER, 22, 0, 0, 0);
		REFERENCE_DATE_UTC_TIMESTAMP = calendar.getTimeInMillis() / 1000 * 1000;
		calendar.set(2017, NOVEMBER, 22, 11, 15, 53);
		REFERENCE_DATE_TIME_UTC_TIMESTAMP = calendar.getTimeInMillis() / 1000 * 1000;
	}

	private DateConverter converter;

	@Before
	public void setUp() {
		converter = new DateConverter.Iso8601DateConverter();
	}

	@Test
	public void shouldFormatFullyQualifiedIso8601ZonedDateTime() {
		final String humanFormat = converter.format(REFERENCE_DATE_TIME_UTC_TIMESTAMP);
		assertEquals(FORMATTED_REFERENCE_DATE_TIME_WITH_TIME_ZONE, humanFormat);
	}

	@Test
	public void shouldParseDateTime() {
		final long timestamp = converter.parse(FORMATTED_REFERENCE_DATE_TIME);
		assertEquals(REFERENCE_DATE_TIME_UTC_TIMESTAMP, timestamp);
	}

	@Test
	public void shouldParseIso8601FullyQualifiedDateTime() {
		final long timestamp = converter.parse(FORMATTED_REFERENCE_DATE_TIME_WITH_TIME_ZONE);
		assertEquals(REFERENCE_DATE_TIME_UTC_TIMESTAMP, timestamp);
	}

	@Test
	public void shouldParseIso8601FullyQualifiedDateTimeWithDifferentTimeZone() {
		final long timestamp = converter.parse(FORMATTED_REFERENCE_DATE_TIME_WITH_ANOTHER_TIME_ZONE);
		assertEquals(REFERENCE_DATE_TIME_UTC_TIMESTAMP, timestamp);
	}

	@Test
	public void shouldBeFormatAndThenDateTimeBackToOriginal() {
		final String dateAsString = converter.format(REFERENCE_DATE_TIME_UTC_TIMESTAMP);
		final long actualTimestamp = converter.parse(dateAsString);
		assertEquals(REFERENCE_DATE_TIME_UTC_TIMESTAMP, actualTimestamp);
	}

	@Test
	public void shouldParseDate() {
		final long timestamp = converter.parse(FORMATTED_REFERENCE_DATE);
		assertEquals(REFERENCE_DATE_UTC_TIMESTAMP, timestamp);
	}

	@Test
	public void shouldParseIso8601FullyQualifiedDate() {
		final long timestamp = converter.parse(FORMATTED_REFERENCE_DATE_WITH_TIME_ZONE);
		assertEquals(REFERENCE_DATE_UTC_TIMESTAMP, timestamp);
	}

	@Test
	public void shouldBeFormatAndThenDateBackToOriginal() {
		final String dateAsString = converter.formatDate(REFERENCE_DATE_UTC_TIMESTAMP);
		final long actualTimestamp = converter.parse(dateAsString);
		assertEquals(REFERENCE_DATE_UTC_TIMESTAMP, actualTimestamp);
	}
}