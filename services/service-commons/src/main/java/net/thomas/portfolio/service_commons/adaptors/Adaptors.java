package net.thomas.portfolio.service_commons.adaptors;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.LegalAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.RenderingAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.UsageAdaptor;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.ModelUtilities;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

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

	private Adaptors(AnalyticsAdaptor analyticsAdaptor, HbaseIndexModelAdaptor hbaseModelAdaptor, LegalAdaptor legalAdaptor, RenderingAdaptor renderingAdaptor,
			UsageAdaptor usageAdaptor, ModelUtilities utilities) {
		this.analyticsAdaptor = analyticsAdaptor;
		this.hbaseModelAdaptor = hbaseModelAdaptor;
		this.legalAdaptor = legalAdaptor;
		this.renderingAdaptor = renderingAdaptor;
		this.usageAdaptor = usageAdaptor;
		this.utilities = utilities;
	}

	/***
	 * Using {@link AnalyticsAdaptor#getKnowledge}<BR>
	 * This method will run a query in the analytics system to fetch relevant information about the selector.
	 *
	 * @param selectorId
	 *            The ID of the selector to query about
	 * @return A summary of the knowledge about this exact selector present in the system
	 */
	public AnalyticalKnowledge getKnowledge(DataTypeId selectorId) {
		return analyticsAdaptor.getKnowledge(selectorId);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getDataTypes}<BR>
	 *
	 * @return All data types present in the current schema
	 */
	public Collection<String> getDataTypes() {
		return hbaseModelAdaptor.getDataTypes();
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getDocumentTypes}<BR>
	 *
	 * @return All document types in the current schema
	 */
	public Collection<String> getDocumentTypes() {
		return hbaseModelAdaptor.getDocumentTypes();
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getSelectorTypes}<BR>
	 *
	 * @return All selector types in the current schema
	 */
	public Collection<String> getSelectorTypes() {
		return hbaseModelAdaptor.getSelectorTypes();
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getIndexedDocumentTypes}<BR>
	 *
	 * @param selectorType
	 *            The selector type in question
	 * @return All document types in the current schema, that can be hit when querying for this selector type
	 */
	public Set<String> getIndexedDocumentTypes(String selectorType) {
		return hbaseModelAdaptor.getIndexedDocumentTypes(selectorType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getIndexedRelations}<BR>
	 *
	 * @param selectorType
	 *            The selector type in question
	 * @return All types of relations in the current schema, that can be hit when querying for this selector type
	 */
	public Set<String> getIndexedRelations(String selectorType) {
		return hbaseModelAdaptor.getIndexedRelations(selectorType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getAllIndexedRelations}<BR>
	 *
	 * @return All types of relations in the current schema, that can be hit when querying for any selector type
	 */
	public Collection<String> getAllIndexedRelations() {
		return hbaseModelAdaptor.getAllIndexedRelations();
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getSelectorSuggestions}<BR>
	 *
	 * @param simpleRepresentation
	 *            A simple, human readable representation of a selector
	 * @return A list of IDs for possible selectors based on the simple representation, ordered by occurrence in the data followed by relevance guess
	 */
	public List<DataTypeId> getSelectorSuggestions(String simpleRepresentation) {
		return hbaseModelAdaptor.getSelectorSuggestions(simpleRepresentation);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getIdFromSimpleRep}<BR>
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
	 * Using {@link HbaseIndexModelAdaptor#isSimpleRepresentable}<BR>
	 *
	 * @param type
	 *            Type of the selector
	 * @return true, if the selector has a simple, human readable representation
	 */
	public boolean isSimpleRepresentable(String dataType) {
		return hbaseModelAdaptor.isSimpleRepresentable(dataType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#isSelector}<BR>
	 *
	 * @param type
	 *            The data type is question
	 * @return true, if this type is a Selector in the current schema
	 */
	public boolean isSelector(String dataType) {
		return hbaseModelAdaptor.isSelector(dataType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#isDocument}<BR>
	 *
	 * @param type
	 *            The data type is question
	 * @return true, if this type is a Document in the current schema
	 */
	public boolean isDocument(String dataType) {
		return hbaseModelAdaptor.isDocument(dataType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getFieldsForDataType}<BR>
	 *
	 * @param dataType
	 *            The data type in question
	 * @return All fields belonging to this type in the current schema
	 */
	public Fields getFieldsForDataType(String dataType) {
		return hbaseModelAdaptor.getFieldsForDataType(dataType);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getDataType}<BR>
	 *
	 * @param id
	 *            Id of the data type to load
	 * @return The complete set of values for this data type with all sub-types
	 */
	public DataType getDataType(DataTypeId id) {
		return hbaseModelAdaptor.getDataType(id);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getReferences}<BR>
	 *
	 * @param documentId
	 *            The ID of the document to load references for
	 * @return All references for this data type in the index
	 */
	public References getReferences(DataTypeId id) {
		return hbaseModelAdaptor.getReferences(id);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#getStatistics}<BR>
	 *
	 * <B>All queries into the selector statistics are validated using the legal service before the query is executed.</B>
	 *
	 * @param selectorId
	 *            The ID of the selector to load statistics for
	 * @return An occurrence count for how often this selector is seen in the data, grouped by a set of time periods from today backwards
	 */
	public Statistics getStatistics(DataTypeId selectorId) {
		return hbaseModelAdaptor.getStatistics(selectorId);
	}

	/***
	 * Using {@link HbaseIndexModelAdaptor#lookupSelectorInInvertedIndex}<BR>
	 *
	 * This method allows for lookups of data in the inverted index using a selector, a set of bounds, legal information and a set of optional filtering
	 * values.<BR>
	 *
	 * <B>All queries into the inverted index are validated using the legal service before the query is executed.</B>
	 *
	 * @param request
	 *            The description of a lookup in the inverted index
	 * @return A list of document information containers ordered by time of event (descending)
	 */
	public DocumentInfos lookupSelectorInInvertedIndex(InvertedIndexLookupRequest request) {
		return hbaseModelAdaptor.lookupSelectorInInvertedIndex(request);
	}

	/***
	 * Using {@link LegalAdaptor#checkLegalityOfSelectorQuery}<BR>
	 *
	 * @param selectorId
	 *            The ID of the selector is being queried
	 * @param legalInfo
	 *            The legal parameters supporting the lookup
	 * @return An assessment of whether the lookup would be legal to complete
	 */
	public Legality checkLegalityOfSelectorQuery(DataTypeId selectorId, LegalInformation legalInfo) {
		return legalAdaptor.checkLegalityOfInvertedIndexQuery(selectorId, legalInfo);
	}

	/***
	 * Using {@link LegalAdaptor#checkLegalityOfStatisticsLookup}<BR>
	 *
	 * @param dataTypeId
	 *            The ID of the selector is being queried
	 * @param legalInfo
	 *            The legal parameters supporting the lookup
	 * @return An assessment of whether the lookup would be legal to complete
	 */
	public Legality checkLegalityOfStatisticsLookup(DataTypeId dataTypeId, LegalInformation legalInfo) {
		return legalAdaptor.checkLegalityOfStatisticsLookup(dataTypeId, legalInfo);
	}

	/***
	 * Using {@link LegalAdaptor#auditLogInvertedIndexLookup}<BR>
	 *
	 * @param selectorId
	 *            The ID of the selector to lookup events for
	 * @param legalInfo
	 *            The legal parameters supporting the lookup
	 * @return True if the logging is successful
	 */
	public Boolean auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return legalAdaptor.auditLogInvertedIndexLookup(selectorId, legalInfo);
	}

	/***
	 * Using {@link LegalAdaptor#auditLogStatisticsLookup}<BR>
	 *
	 * @param selectorId
	 *            The ID of the selector to lookup statistics for
	 * @param legalInfo
	 *            The legal parameters supporting the lookup
	 * @return True if the logging is successful
	 */
	public Boolean auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return legalAdaptor.auditLogStatisticsLookup(selectorId, legalInfo);
	}

	/***
	 * Using {@link RenderingAdaptor#renderAsSimpleRepresentation}<BR>
	 *
	 * @param selectorId
	 *            The ID of the selector to lookup a simple representation for
	 * @return The simple representation, if the selector is known in the index, null otherwise.
	 */
	public String renderAsSimpleRepresentation(DataTypeId selectorId) {
		return renderingAdaptor.renderAsSimpleRepresentation(selectorId);
	}

	/***
	 * Using {@link RenderingAdaptor#renderAsText}<BR>
	 *
	 * @param id
	 *            The ID of the data type to render
	 * @return A textual representation of the type, if the data type exists, null otherwise
	 */
	public String renderAsText(DataTypeId id) {
		return renderingAdaptor.renderAsText(id);
	}

	/***
	 * Using {@link RenderingAdaptor#renderAsHtml}<BR>
	 *
	 * @param id
	 *            The ID of the data type to render
	 * @return An HTML representation of the type, if the data type exists, null otherwise
	 */
	public String renderAsHtml(DataTypeId id) {
		return renderingAdaptor.renderAsHtml(id);
	}

	/***
	 * Using {@link UsageAdaptor#storeUsageActivity}<BR>
	 *
	 * @param documentId
	 *            The ID of the document that has been used
	 * @param activity
	 *            A description of who used the document how, when
	 * @return A copy of the activity entity as it was stored
	 */
	public UsageActivity storeUsageActivity(DataTypeId documentId, UsageActivity activity) {
		return usageAdaptor.storeUsageActivity(documentId, activity);
	}

	/***
	 * Using {@link UsageAdaptor#fetchUsageActivities}<BR>
	 *
	 * @param documentId
	 *            The ID of the document to fetch usage events for
	 * @param bounds
	 *            The parameters for the fetch
	 * @return An ordered (newest first) list of events for the document
	 */
	public UsageActivities fetchUsageActivities(DataTypeId documentId, Bounds bounds) {
		return usageAdaptor.fetchUsageActivities(documentId, bounds);
	}

	/***
	 * Using {@link ModelUtilities#getIec8601DateConverter}<BR>
	 *
	 * @return A thread-safe date converter for parsing and/or formatting dates
	 */
	public DateConverter getIec8601DateConverter() {
		return utilities.getIec8601DateConverter();
	}

	public static class Builder {
		private AnalyticsAdaptor analyticsAdaptor;
		private HbaseIndexModelAdaptor hbaseModelAdaptor;
		private LegalAdaptor legalAdaptor;
		private RenderingAdaptor renderingAdaptor;
		private UsageAdaptor usageAdaptor;

		public Builder() {
			analyticsAdaptor = null;
			hbaseModelAdaptor = null;
			legalAdaptor = null;
			renderingAdaptor = null;
			usageAdaptor = null;
		}

		public Builder setAnalyticsAdaptor(AnalyticsAdaptor analyticsAdaptor) {
			this.analyticsAdaptor = analyticsAdaptor;
			return this;
		}

		public Builder setHbaseModelAdaptor(HbaseIndexModelAdaptor hbaseModelAdaptor) {
			this.hbaseModelAdaptor = hbaseModelAdaptor;
			return this;
		}

		public Builder setLegalAdaptor(LegalAdaptor legalAdaptor) {
			this.legalAdaptor = legalAdaptor;
			return this;
		}

		public Builder setRenderingAdaptor(RenderingAdaptor renderingAdaptor) {
			this.renderingAdaptor = renderingAdaptor;
			return this;
		}

		public Builder setUsageAdaptor(UsageAdaptor usageAdaptor) {
			this.usageAdaptor = usageAdaptor;
			return this;
		}

		public Adaptors build() {
			return new Adaptors(analyticsAdaptor, hbaseModelAdaptor, legalAdaptor, renderingAdaptor, usageAdaptor, new ModelUtilities());
		}
	}
}