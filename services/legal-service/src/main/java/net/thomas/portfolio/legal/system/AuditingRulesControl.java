package net.thomas.portfolio.legal.system;

import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;

import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

public class AuditingRulesControl {

	private AnalyticsAdaptor analyticsAdaptor;

	public AuditingRulesControl() {
	}

	public void setAnalyticsAdaptor(AnalyticsAdaptor analyticsAdaptor) {
		this.analyticsAdaptor = analyticsAdaptor;
	}

	public Legality checkLegalityOfSelectorQuery(DataTypeId selectorId, LegalInformation legalInfo) {
		Legality response = ILLEGAL;
		final AnalyticalKnowledge knowledge = analyticsAdaptor.getKnowledge(selectorId);
		if (userIsInvalid(legalInfo) || justificationIsRequired(knowledge) && justificationIsInvalid(legalInfo)) {
			response = ILLEGAL;
		} else {
			response = LEGAL;
		}
		return response;
	}

	private boolean userIsInvalid(LegalInformation legalInfo) {
		return legalInfo.user == null || legalInfo.user.isEmpty();
	}

	private boolean justificationIsRequired(final AnalyticalKnowledge knowledge) {
		return knowledge.isRestricted == CERTAIN;
	}

	private boolean justificationIsInvalid(LegalInformation legalInfo) {
		return legalInfo.justification == null || legalInfo.justification.isEmpty();
	}
}
