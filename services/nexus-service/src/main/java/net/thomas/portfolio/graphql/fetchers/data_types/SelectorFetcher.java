package net.thomas.portfolio.graphql.fetchers.data_types;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;

public class SelectorFetcher extends EntityFetcher<Selector> {

	private final DateConverter dateFormatter;

	public SelectorFetcher(String type, Adaptors adaptors) {
		super(type, adaptors);
		dateFormatter = adaptors.getDateConverter();
	}

	@Override
	public Selector _get(DataFetchingEnvironment environment) {
		final Object uid = environment.getArgument("uid");
		if (uid != null) {
			final Selector selector = (Selector) adaptors.getDataType(new DataTypeId(type, uid.toString()));
			if (selector != null) {
				if (environment.getArgument("justification") != null) {
					selector.put("justification", environment.getArgument("justification"));
				}
				selector.put("lowerBound", determineAfter(environment.getArguments()));
				selector.put("upperBound", determineBefore(environment.getArguments()));
			}
			return selector;
		}
		return null;
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