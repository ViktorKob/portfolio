package net.thomas.portfolio.shared_objects.adaptors;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.ModelUtilities;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityItem;

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

	public PriorKnowledge getPriorKnowledge(DataTypeId selectorId) {
		return analyticsAdaptor.getPriorKnowledge(selectorId);
	}

	public Collection<String> getDataTypes() {
		return hbaseModelAdaptor.getDataTypes();
	}

	public boolean isSimpleRepresentable(String dataType) {
		return hbaseModelAdaptor.isSimpleRepresentable(dataType);
	}

	public boolean isSelector(String dataType) {
		return hbaseModelAdaptor.isSelector(dataType);
	}

	public boolean isDocument(String dataType) {
		return hbaseModelAdaptor.isDocument(dataType);
	}

	public Collection<Field> getDataTypeFields(String dataType) {
		return hbaseModelAdaptor.getDataTypeFields(dataType);
	}

	public Collection<Indexable> getIndexables(String selector) {
		return hbaseModelAdaptor.getIndexables(selector);
	}

	public DataTypeId getIdFromSimpleRep(String type, String simpleRep) {
		return hbaseModelAdaptor.getIdFromSimpleRep(type, simpleRep);
	}

	public DataType getDataType(DataTypeId id) {
		return hbaseModelAdaptor.getDataType(id);
	}

	public Map<StatisticsPeriod, Long> getStatistics(DataTypeId selectorId) {
		return hbaseModelAdaptor.getStatistics(selectorId);
	}

	public Collection<Reference> getReferences(DataTypeId id) {
		return hbaseModelAdaptor.getReferences(id);
	}

	public List<DocumentInfo> invertedIndexLookup(InvertedIndexLookupRequest request) {
		return hbaseModelAdaptor.invertedIndexLookup(request);
	}

	public Legality checkLegalityOfInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return legalAdaptor.checkLegalityOfInvertedIndexLookup(selectorId, legalInfo);
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
