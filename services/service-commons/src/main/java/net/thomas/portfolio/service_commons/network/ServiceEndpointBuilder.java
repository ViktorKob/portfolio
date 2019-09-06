package net.thomas.portfolio.service_commons.network;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import net.thomas.portfolio.services.ContextPathSection;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class ServiceEndpointBuilder {
	public static ContextPathSection asEndpoint(ContextPathSection prefix, DataTypeId id, ContextPathSection... suffixes) {
		return () -> {
			final String prefixPath = prefix.getContextPath() + "/" + id.getDti_type() + "/" + id.getDti_uid();
			final String suffixPath = asString(suffixes);
			return prefixPath + suffixPath;
		};
	}

	public static ContextPathSection asEndpoint(ContextPathSection prefix, String dataType, ContextPathSection... suffixes) {
		return () -> {
			final String prefixPath = prefix.getContextPath() + "/" + dataType;
			final String suffixPath = asString(suffixes);
			return prefixPath + suffixPath;
		};
	}

	public static ContextPathSection asEndpoint(ContextPathSection firstPrefix, String secondPrefix, ContextPathSection thirdPrefix, String value) {
		return () -> {
			return firstPrefix.getContextPath() + "/" + secondPrefix + thirdPrefix.getContextPath() + "/" + value + "/";
		};
	}

	public static ContextPathSection asEndpoint(ContextPathSection firstPrefix, ContextPathSection secondPrefix) {
		return () -> {
			return firstPrefix.getContextPath() + secondPrefix.getContextPath();
		};
	}

	public static ContextPathSection asEndpoint(ContextPathSection firstPrefix, ContextPathSection secondPrefix, String value) {
		return () -> {
			return firstPrefix.getContextPath() + secondPrefix.getContextPath() + "/" + value + "/";
		};
	}

	private static String asString(ContextPathSection... suffixes) {
		return stream(suffixes).map(ContextPathSection::getContextPath).collect(joining());
	}
}