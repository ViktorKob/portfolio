package net.thomas.portfolio.analytics.system;

import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.POSSIBLY;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.UNLIKELY;

import net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class AnalyticsControl {
	public AnalyticsControl() {
	}

	public AnalyticalKnowledge getPriorKnowledge(DataTypeId id) {
		final ConfidenceLevel recognition = determineFakeRecognizedValue(id);
		final ConfidenceLevel isRestricted = determineFakeIsRestrictedValue(id);
		final String alias = determineFakeAlias(id, recognition);
		return new AnalyticalKnowledge(alias, recognition, isRestricted);
	}

	private ConfidenceLevel determineFakeRecognizedValue(DataTypeId id) {
		final char firstChar = id.uid.charAt(0);
		ConfidenceLevel recognition = UNLIKELY;
		if (firstChar >= 'A' && firstChar <= 'C') {
			recognition = POSSIBLY;
		} else if (firstChar > 'C' && firstChar <= 'F') {
			recognition = CERTAIN;
		}
		return recognition;
	}

	private ConfidenceLevel determineFakeIsRestrictedValue(DataTypeId id) {
		final char secondChar = id.uid.charAt(1);
		ConfidenceLevel isRestricted = UNLIKELY;
		if (secondChar >= 'A' && secondChar <= 'C') {
			isRestricted = POSSIBLY;
		} else if (secondChar > 'C' && secondChar <= 'F') {
			isRestricted = CERTAIN;
		}
		return isRestricted;
	}

	private String determineFakeAlias(DataTypeId id, ConfidenceLevel recognition) {
		String alias = null;
		if (recognition == CERTAIN) {
			alias = "Target " + id.uid.substring(0, 6);
		}
		return alias;
	}
}