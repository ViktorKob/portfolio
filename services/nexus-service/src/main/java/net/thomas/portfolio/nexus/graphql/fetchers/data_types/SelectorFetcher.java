package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalArgumentId.USER_ID;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalArgumentId.JUSTIFICATION;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalArgumentId.LOWER_BOUND_DATE;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalArgumentId.UPPER_BOUND_DATE;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.SelectorIdProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.SelectorProxy;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;

public class SelectorFetcher extends ModelDataFetcher<SelectorProxy<?>> {

	private final DateConverter dateFormatter;
	protected final String type;

	public SelectorFetcher(String type, Adaptors adaptors) {
		super(adaptors);
		this.type = type;
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	@Override
	public SelectorIdProxy get(DataFetchingEnvironment environment) {
		DataTypeId id = null;
		final String uid = environment.getArgument("uid");
		if (uid != null) {
			id = new DataTypeId(type, uid);
		}
		final String simpleRepresentation = environment.getArgument("simpleRep");
		if (simpleRepresentation != null) {
			id = adaptors.getIdFromSimpleRep(type, simpleRepresentation);
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
		proxy.put(USER_ID, environment.getArgument("user"));
		proxy.put(JUSTIFICATION, environment.getArgument("justification"));
		proxy.put(LOWER_BOUND_DATE, determineAfter(environment.getArguments()));
		proxy.put(UPPER_BOUND_DATE, determineBefore(environment.getArguments()));
	}

	private Long determineAfter(Map<String, Object> arguments) {
		Long after = (Long) arguments.get("after");
		if (after == null && arguments.get("afterDate") != null) {
			after = dateFormatter.parseTimestamp((String) arguments.get("afterDate"));
		} else {
			after = Long.MIN_VALUE;
		}
		return after;
	}

	private Long determineBefore(Map<String, Object> arguments) {
		Long before = (Long) arguments.get("before");
		if (before == null && arguments.get("beforeDate") != null) {
			before = dateFormatter.parseTimestamp((String) arguments.get("beforeDate"));
		} else {
			before = Long.MAX_VALUE;
		}
		return before;
	}

}