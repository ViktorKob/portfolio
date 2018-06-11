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
		if (environment.getSource() == null) {
			return null;
		}
		DataTypeId selectorId = null;
		if (environment.getSource() instanceof Selector) {
			selectorId = ((Selector) environment.getSource()).getId();
		} else if (environment.getSource() instanceof DataTypeId) {
			selectorId = environment.getSource();
		}
		final LegalInformation legalInfo = extractLegalInformation(environment.getArguments());
		if (lookupIsIllegal(selectorId, legalInfo)) {
			throw new GraphQLException("Statistics lookup for selector " + selectorId.type + "-" + selectorId.uid + " must be justified by a specific user");
		} else {
			if (adaptors.auditLogStatisticsLookup(selectorId, legalInfo)) {
				final Map<StatisticsPeriod, Long> statistics = adaptors.getStatistics(selectorId);
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