package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.ofInstant;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

public interface DateConverter {

	long parse(String formattedDate);

	String format(Long timestamp);

	String formatDate(Long timestamp);

	public static class Iso8601DateConverter implements DateConverter {
		private final DateTimeFormatter formatter;

		public Iso8601DateConverter() {
			formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
					.append(ISO_LOCAL_DATE)
					.optionalStart()
					.appendLiteral('T')
					.append(DateTimeFormatter.ISO_LOCAL_TIME)
					.optionalEnd()
					.optionalStart()
					.appendOffsetId()
					.optionalEnd()
					.toFormatter();
		}

		@Override
		public long parse(final String formattedDate) {
			final TemporalAccessor temporalAccessor = formatter.parseBest(formattedDate, ZonedDateTime::from, LocalDateTime::from, LocalDate::from);
			if (temporalAccessor instanceof ZonedDateTime) {
				return ((ZonedDateTime) temporalAccessor).toInstant().toEpochMilli();
			} else if (temporalAccessor instanceof LocalDateTime) {
				return ((LocalDateTime) temporalAccessor).atZone(UTC).toInstant().truncatedTo(SECONDS).toEpochMilli();
			} else {
				return ((LocalDate) temporalAccessor).atStartOfDay(UTC).toInstant().truncatedTo(SECONDS).toEpochMilli();
			}
		}

		@Override
		public String format(final Long timestamp) {
			final ZonedDateTime dateTime = ofInstant(ofEpochMilli(timestamp), UTC).truncatedTo(SECONDS);
			return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+00:00";
		}

		@Override
		public String formatDate(final Long timestamp) {
			return LocalDateTime.ofInstant(ofEpochMilli(timestamp), UTC).format(ISO_LOCAL_DATE) + "+00:00";
		}
	}
}
