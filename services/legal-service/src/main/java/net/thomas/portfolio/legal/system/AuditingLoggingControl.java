package net.thomas.portfolio.legal.system;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class AuditingLoggingControl {

	public AuditingLoggingControl() {
	}

	public boolean logInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		// TODO[Thomas]: Pending implementation
		final boolean accepted = true;
		return accepted;
	}

	public boolean logStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		// TODO[Thomas]: Pending implementation
		final boolean accepted = true;
		return accepted;
	}
}
