package net.thomas.portfolio.shared_objects.adaptors;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

public interface LegalAdaptor {
	Legality checkLegalityOfSelectorQuery(DataTypeId selectorId, LegalInformation legalInfo);

	Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo);

	Boolean auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo);
}