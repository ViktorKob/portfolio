package net.thomas.portfolio.shared_objects.adaptors;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.shared_objects.SelectorSearch;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.ModelUtilities;

public class Adaptors {
	private final HbaseModelAdaptor hbaseModelAdaptor;
	private final RenderingAdaptor renderingAdaptor;
	private final AnalyticsAdaptor analyticsAdaptor;
	private final ModelUtilities utilities;

	public Adaptors(HbaseModelAdaptor hbaseModelAdaptor, RenderingAdaptor renderingAdaptor, AnalyticsAdaptor analyticsAdaptor) {
		this.hbaseModelAdaptor = hbaseModelAdaptor;
		this.renderingAdaptor = renderingAdaptor;
		this.analyticsAdaptor = analyticsAdaptor;
		utilities = new ModelUtilities();
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

	public List<DocumentInfo> invertedIndexLookup(SelectorSearch search, Indexable indexable) {
		return hbaseModelAdaptor.invertedIndexLookup(search, indexable);
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

	public PreviousKnowledge getPreviousKnowledgeFor(Selector selector) {
		return analyticsAdaptor.getPreviousKnowledgeFor(selector);
	}

	public DateConverter getDateConverter() {
		return utilities.getDateConverter();
	}
}
