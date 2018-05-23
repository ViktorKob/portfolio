package net.thomas.portfolio.shared_objects.hbase_index.schema;

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
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
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

	public Selector getDataTypeBySimpleRepresentation(String type, String simpleRep) {
		return hbaseModelAdaptor.getDataTypeBySimpleRep(type, simpleRep);
	}

	public DataType getDataTypeByUid(String type, String uid) {
		return hbaseModelAdaptor.getDataTypeByUid(type, uid);
	}

	public Map<StatisticsPeriod, Long> getStatistics(Selector selector) {
		return hbaseModelAdaptor.getStatistics(selector);
	}

	public Collection<Reference> getReferences(Document document) {
		return hbaseModelAdaptor.getReferences(document);
	}

	public List<Document> doSearch(SelectorSearch search, Indexable indexable) {
		return hbaseModelAdaptor.doSearch(search, indexable);
	}

	public String renderAsText(DataType entity) {
		return renderingAdaptor.renderAsText(entity);
	}

	public String renderAsHtml(DataType entity) {
		return renderingAdaptor.renderAsHtml(entity);
	}

	public String renderAsSimpleRepresentation(Selector selector) {
		return renderingAdaptor.renderAsSimpleRepresentation(selector);
	}

	public PreviousKnowledge getPreviousKnowledgeFor(Selector selector) {
		return analyticsAdaptor.getPreviousKnowledgeFor(selector);
	}

	public DateConverter getDateConverter() {
		return utilities.getDateConverter();
	}
}
