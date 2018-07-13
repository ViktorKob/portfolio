package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.UID;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeIdProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class DataTypeFetcher extends ModelDataFetcher<DataTypeIdProxy> {

	private final String type;

	public DataTypeFetcher(String type, Adaptors adaptors) {
		super(adaptors);
		this.type = type;
	}

	@Override
	public DataTypeIdProxy get(DataFetchingEnvironment environment) {
		if (UID.canBeExtractedFrom(environment)) {
			return new DataTypeIdProxy(new DataTypeId(type, UID.extractFrom(environment)), adaptors);
		} else {
			return null;
		}
	}
}