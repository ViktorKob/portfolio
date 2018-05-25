package net.thomas.portfolio.shared_objects.hbase_index.schema;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.IndexableFilter;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParserLibrary;

public class HBaseIndexSchemaSerialization implements HbaseIndexSchema {

	private SimpleRepresentationParserLibrary simpleRepParsers;

	protected Map<String, Map<String, Field>> dataTypeFields;
	protected Set<String> dataTypes;
	protected Set<String> documentTypes;
	protected Set<String> selectorTypes;
	protected Set<String> simpleRepresentableTypes;
	protected Map<String, Collection<Indexable>> indexables;

	public HBaseIndexSchemaSerialization() {
	}

	protected void initialize() {
		simpleRepParsers = new SimpleRepresentationParserLibrary(this);
	}

	public Map<String, Map<String, Field>> getDataTypeFields() {
		return dataTypeFields;
	}

	public void setDataTypeFields(Map<String, Map<String, Field>> dataTypeFields) {
		this.dataTypeFields = dataTypeFields;
	}

	@Override
	public Set<String> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(Set<String> dataTypes) {
		this.dataTypes = dataTypes;
	}

	@Override
	public Set<String> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(Set<String> documentTypes) {
		this.documentTypes = documentTypes;
	}

	@Override
	public Set<String> getSelectorTypes() {
		return selectorTypes;
	}

	public void setSelectorTypes(Set<String> selectorTypes) {
		this.selectorTypes = selectorTypes;
	}

	@Override
	public Set<String> getSimpleRepresentableTypes() {
		return simpleRepresentableTypes;
	}

	public void setSimpleRepresentableTypes(Set<String> simpleRepresentableTypes) {
		this.simpleRepresentableTypes = simpleRepresentableTypes;
	}

	public Map<String, Collection<Indexable>> getIndexables() {
		return indexables;
	}

	public void setIndexables(Map<String, Collection<Indexable>> indexables) {
		this.indexables = indexables;
	}

	@Override
	public Collection<Indexable> getIndexables(String selectorType, IndexableFilter... filters) {
		Collection<Indexable> indexables = this.indexables.get(selectorType);
		for (final IndexableFilter filter : filters) {
			indexables = filter.filter(indexables);
		}
		return indexables;
	}

	@Override
	public Collection<Field> getFieldsForDataType(String dataType) {
		return dataTypeFields.get(dataType)
			.values();
	}

	@Override
	public Field getFieldForIndexable(Indexable indexable) {
		return dataTypeFields.get(indexable.documentType)
			.get(indexable.documentField);
	}

	@Override
	@JsonIgnore
	public String calculateUid(String type, String simpleRep) {
		return simpleRepParsers.parse(type, simpleRep)
			.getId()
			.getUid();
	}
}