package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static java.util.stream.Collectors.toList;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.SIMPLE_REP;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.extractFirstThatIsPresent;
import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalServiceArgumentId.USER_ID;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId.JUSTIFICATION;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId.LOWER_BOUND_DATE;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId.UPPER_BOUND_DATE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;
import net.thomas.portfolio.nexus.graphql.data_proxies.SelectorIdProxy;
import net.thomas.portfolio.nexus.graphql.fetchers.GlobalServiceArgumentId;
import net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.ServiceArgument;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class SelectorSuggestionsFetcher extends ModelDataFetcher<List<SelectorIdProxy>> {

	public SelectorSuggestionsFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public List<SelectorIdProxy> get(DataFetchingEnvironment environment) {
		final List<DataTypeId> selectorSuggestionIds = adaptors.getSelectorSuggestions(SIMPLE_REP.extractFrom(environment));
		final Map<ServiceArgument<?>, Object> arguments = extractArguments(environment);
		return convert(selectorSuggestionIds, arguments);
	}

	protected Map<ServiceArgument<?>, Object> extractArguments(DataFetchingEnvironment environment) {
		final Map<ServiceArgument<?>, Object> arguments = new HashMap<>();
		arguments.put(USER_ID, GraphQlArgument.USER.extractFrom(environment));
		arguments.put(JUSTIFICATION, GraphQlArgument.JUSTIFICATION.extractFrom(environment));
		arguments.put(LOWER_BOUND_DATE, extractFirstThatIsPresent(environment, MIN_VALUE, AFTER, AFTER_DATE));
		arguments.put(UPPER_BOUND_DATE, extractFirstThatIsPresent(environment, MAX_VALUE, BEFORE, BEFORE_DATE));
		return arguments;
	}

	private List<SelectorIdProxy> convert(List<DataTypeId> selectorIds, Map<ServiceArgument<?>, Object> arguments) {
		return selectorIds.stream()
			.map(id -> new SelectorIdProxy(id, adaptors))
			.peek(proxy -> decorateWithArguments(proxy, arguments))
			.collect(toList());
	}

	private void decorateWithArguments(SelectorIdProxy proxy, Map<ServiceArgument<?>, Object> arguments) {
		for (final Entry<ServiceArgument<?>, Object> entry : arguments.entrySet()) {
			if (entry.getKey() instanceof GlobalServiceArgumentId) {
				proxy.put((GlobalServiceArgumentId) entry.getKey(), entry.getValue());
			} else if (entry.getKey() instanceof LocalServiceArgumentId) {
				proxy.put((LocalServiceArgumentId) entry.getKey(), entry.getValue());
			}
		}
	}
}