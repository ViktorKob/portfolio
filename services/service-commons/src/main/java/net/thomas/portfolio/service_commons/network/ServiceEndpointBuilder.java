package net.thomas.portfolio.service_commons.network;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import net.thomas.portfolio.services.ServiceEndpoint;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class ServiceEndpointBuilder {
	public static ServiceEndpoint asEndpoint(ServiceEndpoint prefix, DataTypeId id, ServiceEndpoint... suffixes) {
		return () -> {
			final String prefixPath = prefix.getContextPath() + "/" + id.getDti_type() + "/" + id.getDti_uid();
			final String suffixPath = asString(suffixes);
			return prefixPath + suffixPath;
		};
	}

	public static ServiceEndpoint asEndpoint(ServiceEndpoint prefix, String dataType, ServiceEndpoint... suffixes) {
		return () -> {
			final String prefixPath = prefix.getContextPath() + "/" + dataType;
			final String suffixPath = asString(suffixes);
			return prefixPath + suffixPath;
		};
	}

	public static ServiceEndpoint asEndpoint(ServiceEndpoint firstPrefix, ServiceEndpoint secondPrefix, String value) {
		return () -> {
			return firstPrefix.getContextPath() + secondPrefix.getContextPath() + "/" + value + "/";
		};
	}

	private static String asString(ServiceEndpoint... suffixes) {
		return stream(suffixes).map(ServiceEndpoint::getContextPath)
			.collect(joining());
	}
}