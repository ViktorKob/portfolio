package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.OPTIONAL_SIMPLE_REP;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.OPTIONAL_UID;
import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalServiceArgumentId.USER_ID;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId.JUSTIFICATION;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId.LOWER_BOUND_DATE;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId.UPPER_BOUND_DATE;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;
import net.thomas.portfolio.nexus.graphql.data_proxies.SelectorIdProxy;
import net.thomas.portfolio.nexus.graphql.data_proxies.SelectorProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class SelectorFetcher extends ModelDataFetcher<SelectorProxy<?>> {

	protected final String type;

	public SelectorFetcher(String type, Adaptors adaptors) {
		super(adaptors);
		this.type = type;
	}

	@Override
	public SelectorIdProxy get(DataFetchingEnvironment environment) {
		DataTypeId id = null;
		if (OPTIONAL_UID.canBeExtractedFrom(environment)) {
			id = new DataTypeId(type, OPTIONAL_UID.extractFrom(environment));
		}
		if (OPTIONAL_SIMPLE_REP.canBeExtractedFrom(environment)) {
			id = adaptors.getIdFromSimpleRep(type, OPTIONAL_SIMPLE_REP.extractFrom(environment));
		}
		if (id != null) {
			final SelectorIdProxy proxy = new SelectorIdProxy(id, adaptors);
			decorateWithSelectorParameters(proxy, environment);
			return proxy;
		} else {
			return null;
		}
	}

	protected void decorateWithSelectorParameters(SelectorProxy<?> proxy, DataFetchingEnvironment environment) {
		proxy.put(USER_ID, GraphQlArgument.USER.extractFrom(environment));
		proxy.put(JUSTIFICATION, GraphQlArgument.JUSTIFICATION.extractFrom(environment));
		proxy.put(LOWER_BOUND_DATE, determineAfter(environment));
		proxy.put(UPPER_BOUND_DATE, determineBefore(environment));
	}

	private Long determineAfter(DataFetchingEnvironment environment) {
		Long after = AFTER.extractFrom(environment);
		if (after == null && AFTER_DATE.canBeExtractedFrom(environment)) {
			after = AFTER_DATE.extractFrom(environment);
		} else {
			after = Long.MIN_VALUE;
		}
		return after;
	}

	private Long determineBefore(DataFetchingEnvironment environment) {
		Long before = BEFORE.extractFrom(environment);
		if (before == null && BEFORE_DATE.canBeExtractedFrom(environment)) {
			before = BEFORE_DATE.extractFrom(environment);
		} else {
			before = Long.MAX_VALUE;
		}
		return before;
	}
}