package net.thomas.portfolio.shared_objects.adaptors;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.ModelUtilities;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityItem;

/***
 * This collection of adaptors allows usage of all endpoints in the service infrastructure as java methods.
 */
public class Adaptors {
	private final AnalyticsAdaptor analyticsAdaptor;
	private final HbaseIndexModelAdaptor hbaseModelAdaptor;
	private final LegalAdaptor legalAdaptor;
	private final RenderingAdaptor renderingAdaptor;
	private final UsageAdaptor usageAdaptor;
	private final ModelUtilities utilities;

	public Adaptors(AnalyticsAdaptor analyticsAdaptor, HbaseIndexModelAdaptor hbaseModelAdaptor, LegalAdaptor legalAdaptor, RenderingAdaptor renderingAdaptor,
			UsageAdaptor usageAdaptor) {
		this.analyticsAdaptor = analyticsAdaptor;
		this.hbaseModelAdaptor = hbaseModelAdaptor;
		this.legalAdaptor = legalAdaptor;
		this.renderingAdaptor = renderingAdaptor;
		this.usageAdaptor = usageAdaptor;
		utilities = new ModelUtilities();
	}

	/***
	 * Using {@link AnalyticsAdaptor#getPriorKnowledge}
	 *
	 * @param selectorId
	 *            The ID of the selector to query about
	 * @return A summary of the knowledge about this exact selector present in the system
	 */
	public PriorKnowledge getPriorKnowledge(DataTypeId selectorId) {
		return analyticsAdaptor.getPriorKnowledge(selectorId);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getDataTypes}
	 *
	 * @return All data types present in the current schema
	 */
	public Collection<String> getDataTypes() {
		return hbaseModelAdaptor.getDataTypes();
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getDocumentTypes}
	 *
	 * @return All document types in the current schema
	 */
	public Collection<String> getDocumentTypes() {
		return hbaseModelAdaptor.getDocumentTypes();
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getSelectorTypes}
	 *
	 * @return All selector types in the current schema
	 */
	public Collection<String> getSelectorTypes() {
		return hbaseModelAdaptor.getSelectorTypes();
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getIndexedDocumentTypes}
	 *
	 * @param selectorType
	 *            The selector type in question
	 * @return All document types in the current schema, that can be hit when querying for this selector type
	 */
	public Collection<String> getIndexedDocumentTypes(String selectorType) {
		return hbaseModelAdaptor.getIndexedDocumentTypes(selectorType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getIndexedRelationTypes}
	 *
	 * @param selectorType
	 *            The selector type in question
	 * @return All types of relations in the current schema, that can be hit when querying for this selector type
	 */
	public Collection<String> getIndexedRelationTypes(String selectorType) {
		return hbaseModelAdaptor.getIndexedRelations(selectorType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getSelectorSuggestions}
	 *
	 * @param simpleRepresentation
	 *            A simple, human readable representation of a selector
	 * @return A list of possible selectors based on the simple representation, ordered by occurrence in the data followed by relevance guess
	 */
	public List<Selector> getSelectorSuggestions(String simpleRepresentation) {
		return hbaseModelAdaptor.getSelectorSuggestions(simpleRepresentation);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getIdFromSimpleRep}
	 *
	 * @param type
	 *            Type of the selector
	 * @param simpleRep
	 *            A simple, human readable representation of the selector
	 * @return The ID for the selector in the current schema
	 */
	public DataTypeId getIdFromSimpleRep(String type, String simpleRep) {
		return hbaseModelAdaptor.getIdFromSimpleRep(type, simpleRep);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#isSimpleRepresentable}
	 *
	 * @param type
	 *            Type of the selector
	 * @return true, if the selector has a simple, human readable representation
	 */
	public boolean isSimpleRepresentable(String dataType) {
		return hbaseModelAdaptor.isSimpleRepresentable(dataType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#isSelector}
	 *
	 * @param type
	 *            The data type is question
	 * @return true, if this type is a Selector in the current schema
	 */
	public boolean isSelector(String dataType) {
		return hbaseModelAdaptor.isSelector(dataType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#isDocument}
	 *
	 * @param type
	 *            The data type is question
	 * @return true, if this type is a Document in the current schema
	 */
	public boolean isDocument(String dataType) {
		return hbaseModelAdaptor.isDocument(dataType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getDataTypeFields}
	 *
	 * @param dataType
	 *            The data type in question
	 * @return All fields belonging to this type in the current schema
	 */
	public Collection<Field> getDataTypeFields(String dataType) {
		return hbaseModelAdaptor.getDataTypeFields(dataType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getDataType}
	 *
	 * @param id
	 *            Id of the data type to load
	 * @return The complete set of values for this data type with all sub-types
	 */
	public DataType getDataType(DataTypeId id) {
		return hbaseModelAdaptor.getDataType(id);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getReferences}
	 *
	 * @param documentId
	 *            The ID of the document to load references for
	 * @return All references for this data type in the index
	 */
	public Collection<Reference> getReferences(DataTypeId id) {
		return hbaseModelAdaptor.getReferences(id);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getStatistics}
	 *
	 * <B>All queries into the selector statistics are validated using the legal service before the query is executed.</B>
	 *
	 * @param selectorId
	 *            The ID of the selector to load statistics for
	 * @return An occurrence count for how often this selector is seen in the data, grouped by a set of time periods from today backwards
	 */
	public Map<StatisticsPeriod, Long> getStatistics(DataTypeId selectorId) {
		return hbaseModelAdaptor.getStatistics(selectorId);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#lookupSelectorInInvertedIndex}
	 *
	 * This method allows for lookups of data using the inverted index using selectors and bounds.<BR>
	 *
	 * <B>All queries into the inverted index are validated using the legal service before the query is executed.</B>
	 *
	 * @param request
	 *            The description of a lookup in the inverted index
	 * @return A list of document information containers ordered by time of event (descending)
	 */
	public List<DocumentInfo> lookupSelectorInInvertedIndex(InvertedIndexLookupRequest request) {
		return hbaseModelAdaptor.lookupSelectorInInvertedIndex(request);
	}

	public Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return legalAdaptor.auditLogInvertedIndexLookup(selectorId, legalInfo);
	}

	public Boolean auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return legalAdaptor.auditLogStatisticsLookup(selectorId, legalInfo);
	}

	public Legality checkLegalityOfSelectorQuery(DataTypeId selectorId, LegalInformation legalInfo) {
		return legalAdaptor.checkLegalityOfSelectorQuery(selectorId, legalInfo);
	}

	public String renderAsSimpleRepresentation(DataTypeId id) {
		return renderingAdaptor.renderAsSimpleRepresentation(id);
	}

	public String renderAsText(DataTypeId id) {
		return renderingAdaptor.renderAsText(id);
	}

	public String renderAsHtml(DataTypeId id) {
		return renderingAdaptor.renderAsHtml(id);
	}

	public boolean storeUsageActivity(DataTypeId documentId, UsageActivityItem item) {
		return usageAdaptor.storeUsageActivity(documentId, item);
	}

	public List<UsageActivityItem> storeUsageActivity(DataTypeId documentId, Integer offset, Integer limit) {
		return usageAdaptor.fetchUsageActivity(documentId, offset, limit);
	}

	public DateConverter getDateConverter() {
		return utilities.getDateConverter();
	}
}
