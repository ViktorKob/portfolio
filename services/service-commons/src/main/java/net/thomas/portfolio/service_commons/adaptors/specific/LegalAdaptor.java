package net.thomas.portfolio.service_commons.adaptors.specific;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

public interface LegalAdaptor {
	/***
	 * @param selectorId
	 *            The ID of the selector is being queried
	 * @param legalInfo
	 *            The legal parameters supporting the lookup
	 * @return An assessment of whether the lookup would be legal to complete
	 */
	Legality checkLegalityOfInvertedIndexQuery(DataTypeId selectorId, LegalInformation legalInfo);

	/***
	 * @param dataTypeId
	 *            The ID of the selector is being queried
	 * @param legalInfo
	 *            The legal parameters supporting the lookup
	 * @return An assessment of whether the lookup would be legal to complete
	 */
	Legality checkLegalityOfStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo);

	/***
	 * @param selectorId
	 *            The ID of the selector to lookup events for
	 * @param legalInfo
	 *            The legal parameters supporting the lookup
	 * @return True if the logging is successful
	 */
	Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo);

	/***
	 * @param selectorId
	 *            The ID of the selector to lookup statistics for
	 * @param legalInfo
	 *            The legal parameters supporting the lookup
	 * @return True if the logging is successful
	 */
	Boolean auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo);
}