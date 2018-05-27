package net.thomas.portfolio.nexus.graphql.fetchers.statistics;

import static java.util.Collections.emptyMap;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;

import java.util.Map;

import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class SelectorStatisticsFetcher extends ModelDataFetcher<Map<StatisticsPeriod, Long>> {

	public SelectorStatisticsFetcher(Adaptors adaptors) {
		super(adaptors/* , 50 */);
	}

	@Override
	public Map<StatisticsPeriod, Long> _get(DataFetchingEnvironment environment) {
		final LegalInformation legalInfo = extractLegalInformation(environment.getArguments());
		final DataTypeId id = ((Selector) environment.getSource()).getId();
		if (lookupIsIllegal(id, legalInfo)) {
			throw new GraphQLException("Statistics lookup for selector " + id.type + "-" + id.uid + " must be justified by a specific user");
		} else {
			if (adaptors.auditLogStatisticsLookup(id, legalInfo)) {
				final Map<StatisticsPeriod, Long> statistics = adaptors.getStatistics(id);
				if (statistics != null) {
					return statistics;
				}
			}
			return emptyMap();
		}
	}

	private boolean lookupIsIllegal(final DataTypeId selectorId, final LegalInformation legalInfo) {
		return ILLEGAL == adaptors.checkLegalityOfSelectorQuery(selectorId, legalInfo);
	}

	private LegalInformation extractLegalInformation(Map<String, Object> arguments) {
		final String user = (String) arguments.get("user");
		final String justification = (String) arguments.get("justification");
		return new LegalInformation(user, justification, null, null);
	}
}