package net.thomas.portfolio.nexus.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeProxy;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public abstract class ModelDataFetcher<CONTENTS> implements DataFetcher<CONTENTS> {

	protected final Adaptors adaptors;

	public ModelDataFetcher(Adaptors adaptors) {
		this.adaptors = adaptors;
	}

	protected DataTypeProxy<?, ?> getProxy(DataFetchingEnvironment environment) {
		return environment.getSource();
	}

	protected DataTypeId getId(DataFetchingEnvironment environment) {
		return getProxy(environment).getId();
	}

	protected DataType getEntity(DataFetchingEnvironment environment) {
		return getProxy(environment).getEntity();
	}

	protected <T> T getFromEnvironmentOrProxy(DataFetchingEnvironment environment, final GraphQlArgument argument, GlobalServiceArgumentId storedArgument) {
		if (argument.canBeExtractedFrom(environment)) {
			return argument.extractFrom(environment);
		} else {
			return getProxy(environment).get(storedArgument);
		}
	}

	protected <T> T getFromEnvironmentOrProxy(DataFetchingEnvironment environment, final GraphQlArgument argument, LocalServiceArgumentId storedArgument) {
		if (argument.canBeExtractedFrom(environment)) {
			return argument.extractFrom(environment);
		} else {
			return getProxy(environment).get(storedArgument);
		}
	}
}
