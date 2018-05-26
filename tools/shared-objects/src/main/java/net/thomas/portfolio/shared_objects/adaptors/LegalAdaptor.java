package net.thomas.portfolio.shared_objects.adaptors;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

public interface LegalAdaptor {
	Legality checkLegalityOfInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo);
}