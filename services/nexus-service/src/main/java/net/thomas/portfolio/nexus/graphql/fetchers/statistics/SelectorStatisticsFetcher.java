package net.thomas.portfolio.nexus.graphql.fetchers.statistics;

import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalServiceArgumentId.USER_ID;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId.JUSTIFICATION;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;
import net.thomas.portfolio.nexus.graphql.fetchers.IllegalLookupException;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

public class SelectorStatisticsFetcher extends ModelDataFetcher<Statistics> {

	public SelectorStatisticsFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Statistics get(DataFetchingEnvironment environment) {
		if (environment.getSource() == null) {
			return null;
		}
		final DataTypeId selectorId = getId(environment);
		final LegalInformation legalInfo = extractLegalInformation(environment);
		if (lookupIsIllegal(selectorId, legalInfo)) {
			throw new IllegalLookupException(
					"Statistics lookup for selector " + selectorId.type + "-" + selectorId.uid + " must be justified by a specific user");
		} else {
			if (adaptors.auditLogStatisticsLookup(selectorId, legalInfo)) {
				return adaptors.getStatistics(selectorId);
			} else {
				return new Statistics();
			}
		}
	}

	private LegalInformation extractLegalInformation(DataFetchingEnvironment environment) {
		final String user = getFromEnvironmentOrProxy(environment, GraphQlArgument.USER, USER_ID);
		final String justification = getFromEnvironmentOrProxy(environment, GraphQlArgument.JUSTIFICATION, JUSTIFICATION);
		return new LegalInformation(user, justification, null, null);
	}

	private boolean lookupIsIllegal(final DataTypeId selectorId, final LegalInformation legalInfo) {
		final Legality legality = adaptors.checkLegalityOfStatisticsLookup(selectorId, legalInfo);
		return legality == null || ILLEGAL == legality;
	}
}