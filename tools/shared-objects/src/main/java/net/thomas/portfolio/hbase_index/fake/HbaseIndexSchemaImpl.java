package net.thomas.portfolio.hbase_index.fake;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class HbaseIndexSchemaImpl implements HbaseIndexSchema, HBaseIndexSchemaSerialization {

	protected Map<String, Map<String, Field>> dataTypeFields;
	protected Set<String> dataTypes;
	protected Set<String> documentTypes;
	protected Set<String> selectorTypes;
	protected Set<String> simpleRepresentableTypes;
	protected Map<String, List<Indexable>> indexables;

	public HbaseIndexSchemaImpl() {
	}

	@Override
	public Map<String, Map<String, Field>> getDataTypeFields() {
		return dataTypeFields;
	}

	@Override
	public void setDataTypeFields(Map<String, Map<String, Field>> dataTypeFields) {
		this.dataTypeFields = dataTypeFields;
	}

	@Override
	public Set<String> getDataTypes() {
		return dataTypes;
	}

	@Override
	public void setDataTypes(Set<String> dataTypes) {
		this.dataTypes = dataTypes;
	}

	@Override
	public Set<String> getDocumentTypes() {
		return documentTypes;
	}

	@Override
	public void setDocumentTypes(Set<String> documentTypes) {
		this.documentTypes = documentTypes;
	}

	@Override
	public Set<String> getSelectorTypes() {
		return selectorTypes;
	}

	@Override
	public void setSelectorTypes(Set<String> selectorTypes) {
		this.selectorTypes = selectorTypes;
	}

	@Override
	public Set<String> getSimpleRepresentableTypes() {
		return simpleRepresentableTypes;
	}

	@Override
	public void setSimpleRepresentableTypes(Set<String> simpleRepresentableTypes) {
		this.simpleRepresentableTypes = simpleRepresentableTypes;
	}

	@Override
	public Map<String, List<Indexable>> getIndexables() {
		return indexables;
	}

	@Override
	public void setIndexables(Map<String, List<Indexable>> indexables) {
		this.indexables = indexables;
	}

	@Override
	public Collection<Indexable> getIndexables(String selectorType) {
		return indexables.get(selectorType);
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
}