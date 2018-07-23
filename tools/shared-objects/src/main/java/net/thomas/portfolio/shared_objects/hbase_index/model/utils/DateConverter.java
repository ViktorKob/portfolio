package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface DateConverter {

	Date parse(String formattedDate);

	long parseTimestamp(String formattedDate);

	String format(Date date);

	String formatDate(Date date);

	String formatTimestamp(Long timestamp);

	String formatDateTimestamp(Long timestamp);

	public static class Iec8601DateConverter implements DateConverter {
		private final ThreadLocal<SimpleDateFormat> completeFormatter = new ThreadLocal<SimpleDateFormat>() {
			@Override
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			}
		};

		private final ThreadLocal<SimpleDateFormat> dateFormatter = new ThreadLocal<SimpleDateFormat>() {
			@Override
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat("yyyy-MM-dd");
			}
		};

		@Override
		public Date parse(String formattedDate) {
			try {
				if (formattedDate.length() <= 12) {
					return dateFormatter.get()
						.parse(formattedDate);
				} else {
					return completeFormatter.get()
						.parse(formattedDate);
				}
			} catch (final ParseException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public long parseTimestamp(String formattedDate) {
			return parse(formattedDate).getTime();
		}

		@Override
		public String format(Date date) {
			return completeFormatter.get()
				.format(date);
		}

		@Override
		public String formatTimestamp(Long timestamp) {
			return completeFormatter.get()
				.format(new Date(timestamp));
		}

		@Override
		public String formatDate(Date date) {
			return dateFormatter.get()
				.format(date);
		}

		@Override
		public String formatDateTimestamp(Long timestamp) {
			return dateFormatter.get()
				.format(new Date(timestamp));
		}
	}
}
