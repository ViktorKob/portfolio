package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalArgumentId.USER_ID;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.DocumentIdProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.DocumentProxy;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class DocumentFetcher extends ModelDataFetcher<DocumentProxy<?>> {

	private final String type;

	public DocumentFetcher(String type, Adaptors adaptors) {
		super(adaptors);
		this.type = type;
	}

	@Override
	public DocumentProxy<?> get(DataFetchingEnvironment environment) {
		final DocumentIdProxy proxy = new DocumentIdProxy(new DataTypeId(type, environment.getArgument("uid")), adaptors);
		proxy.put(USER_ID, environment.getArgument("user"));
		return proxy;
	}
}