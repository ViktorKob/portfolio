package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.UID;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.USER;
import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalServiceArgumentId.USER_ID;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentIdProxy;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class DocumentFetcher extends ModelDataFetcher<DocumentProxy<?>> {

	private final String type;

	public DocumentFetcher(String type, Adaptors adaptors) {
		super(adaptors);
		this.type = type;
	}

	@Override
	public DocumentProxy<?> get(DataFetchingEnvironment environment) {
		final DocumentIdProxy proxy = new DocumentIdProxy(new DataTypeId(type, UID.extractFrom(environment)), adaptors);
		proxy.put(USER_ID, USER.extractFrom(environment));
		return proxy;
	}
}