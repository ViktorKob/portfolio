package net.thomas.portfolio.legal.system;

import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;

import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

public class LegalRulesControl {

	private AnalyticsAdaptor analyticsAdaptor;
	private final UserMustBeSpecifiedComplianceCheck userComplianceCheck;
	private final JustificationMatchesRequirementsRule justificationComplianceCheck;

	public LegalRulesControl() {
		userComplianceCheck = new UserMustBeSpecifiedComplianceCheck();
		justificationComplianceCheck = new JustificationMatchesRequirementsRule();
	}

	public void setAnalyticsAdaptor(AnalyticsAdaptor analyticsAdaptor) {
		this.analyticsAdaptor = analyticsAdaptor;
	}

	public Legality checkLegalityOfInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (userComplianceCheck.complies(selectorId, legalInfo) && justificationComplianceCheck.complies(selectorId, legalInfo)) {
			return LEGAL;
		} else {
			return ILLEGAL;
		}
	}

	public Legality checkLegalityOfStatisticsLookup(DataTypeId id, LegalInformation legalInfo) {
		if (userComplianceCheck.complies(id, legalInfo) && justificationComplianceCheck.complies(id, legalInfo)) {
			return LEGAL;
		} else {
			return ILLEGAL;
		}
	}

	class UserMustBeSpecifiedComplianceCheck extends LegalComplianceCheck {
		@Override
		public boolean complies(DataTypeId id, LegalInformation legalInfo) {
			return userIsValid(legalInfo);
		}
	}

	class JustificationMatchesRequirementsRule extends LegalComplianceCheck {
		@Override
		public boolean complies(DataTypeId id, LegalInformation legalInfo) {
			final AnalyticalKnowledge knowledge = analyticsAdaptor.getKnowledge(id);
			return justificationIsUnneccessary(knowledge) || justificationIsValid(legalInfo);
		}
	}

	abstract class LegalComplianceCheck {
		public abstract boolean complies(DataTypeId id, LegalInformation legalInfo);

		protected boolean userIsValid(LegalInformation legalInfo) {
			return legalInfo.user != null && !legalInfo.user.isEmpty();
		}

		protected boolean justificationIsUnneccessary(final AnalyticalKnowledge knowledge) {
			return knowledge.isRestricted != CERTAIN;
		}

		protected boolean justificationIsValid(LegalInformation legalInfo) {
			return legalInfo.justification != null && !legalInfo.justification.isEmpty();
		}
	}
}
