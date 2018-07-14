package net.thomas.portfolio.nexus.graphql.fetchers.fields;

import com.fasterxml.jackson.core.JsonProcessingException;

import graphql.GraphQLException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeProxy;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public class RawFormFetcher implements DataFetcher<String> {
	@Override
	public String get(DataFetchingEnvironment environment) {
		try {
			final DataType entity = ((DataTypeProxy<?, ?>) environment.getSource()).getEntity();
			return entity == null ? null : entity.getInRawForm();
		} catch (final JsonProcessingException e) {
			throw new GraphQLException("Unable to extract raw form for data type", e);
		}
	}
}