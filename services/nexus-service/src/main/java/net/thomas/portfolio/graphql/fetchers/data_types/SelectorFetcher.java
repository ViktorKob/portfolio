package net.thomas.portfolio.graphql.fetchers.data_types;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.hbase_index.GraphQlUtilities;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class SelectorFetcher extends EntityFetcher<Selector> {

	private final DateConverter dateFormatter;

	public SelectorFetcher(String type, HbaseModelAdaptor adaptor, GraphQlUtilities utilities) {
		super(type, adaptor);
		dateFormatter = utilities.getDateConverter();
	}

	@Override
	public Selector _get(DataFetchingEnvironment environment) {
		final Object uid = environment.getArgument("uid");
		if (uid != null) {
			final Selector selector = (Selector) adaptor.getDataTypeByUid(type, uid.toString());
			if (selector != null) {
				if (environment.getArgument("justification") != null) {
					selector.setJustification(environment.getArgument("justification"));
				}
				selector.setLowerBound(determineAfter(environment.getArguments()));
				selector.setUpperBound(determineBefore(environment.getArguments()));
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