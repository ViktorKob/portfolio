package net.thomas.portfolio.nexus.graphql.fetchers.statistics;

import static net.thomas.portfolio.shared_objects.analytics.RecognitionLevel.KNOWN;

import java.util.Collections;
import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SelectorStatisticsFetcher extends ModelDataFetcher<Map<StatisticsPeriod, Long>> {

	public SelectorStatisticsFetcher(Adaptors adaptors) {
		super(adaptors/* , 50 */);
	}

	@Override
	public Map<StatisticsPeriod, Long> _get(DataFetchingEnvironment environment) {
		final Selector selector = (Selector) environment.getSource();

		if (isDanish(selector.getId()) && justificationIsMissing(selector)) {
			return Collections.emptyMap();
		}
		final Map<StatisticsPeriod, Long> statistics = adaptors.getStatistics(selector.getId());
		if (statistics != null) {
			return statistics;
		} else {
			return Collections.emptyMap();
		}
	}

	private boolean isDanish(final DataTypeId selectorId) {
		return KNOWN == adaptors.getPreviousKnowledgeFor(selectorId).isDanish;
	}

	private boolean justificationIsMissing(final Selector selector) {
		return selector.get("justification") == null || selector.get("justification")
			.toString()
			.trim()
			.isEmpty();
	}
}