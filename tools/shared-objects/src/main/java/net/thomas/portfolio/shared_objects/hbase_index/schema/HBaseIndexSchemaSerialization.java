package net.thomas.portfolio.shared_objects.hbase_index.schema;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParserLibrary;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HBaseIndexSchemaSerialization implements HbaseIndexSchema {

	// TODO[Thomas]: These must also be serialized to be truly model agnostic
	@JsonIgnore
	private SimpleRepresentationParserLibrary simpleRepParsers;

	protected Map<String, Map<String, Field>> dataTypeFields;
	protected Set<String> dataTypes;
	protected Set<String> documentTypes;
	protected Set<String> selectorTypes;
	protected Set<String> simpleRepresentableTypes;
	protected Map<String, Collection<Indexable>> indexables;
	protected Map<String, Collection<String>> indexableDocumentTypes;
	protected Map<String, Collection<String>> indexableRelations;

	public HBaseIndexSchemaSerialization() {
	}

	public void initialize() {
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
	public Collection<Field> getFieldsForDataType(String dataType) {
		return dataTypeFields.get(dataType)
			.values();
	}

	@Override
	public Set<String> getSimpleRepresentableTypes() {
		return simpleRepresentableTypes;
	}

	public void setSimpleRepresentableTypes(Set<String> simpleRepresentableTypes) {
		this.simpleRepresentableTypes = simpleRepresentableTypes;
	}

	public Map<String, Collection<String>> getIndexableDocumentTypes() {
		return indexableDocumentTypes;
	}

	public void setIndexableDocumentTypes(Map<String, Collection<String>> indexableDocumentTypes) {
		this.indexableDocumentTypes = indexableDocumentTypes;
	}

	@Override
	@JsonIgnore
	public Collection<String> getIndexableDocumentTypes(String selectorType) {
		return indexableDocumentTypes.get(selectorType);
	}

	public Map<String, Collection<String>> getIndexableRelations() {
		return indexableRelations;
	}

	public void setIndexableRelations(Map<String, Collection<String>> indexableRelations) {
		this.indexableRelations = indexableRelations;
	}

	@Override
	@JsonIgnore
	public Collection<String> getIndexableRelations(String selectorType) {
		return indexableRelations.get(selectorType);
	}

	public Collection<Indexable> getIndexables(String selectorType) {
		return indexables.get(selectorType);
	}

	@JsonIgnore
	public Field getFieldForIndexable(Indexable indexable) {
		return dataTypeFields.get(indexable.documentType)
			.get(indexable.documentField);
	}

	@Override
	@JsonIgnore
	public List<DataTypeId> getSelectorSuggestions(String selectorString) {
		final LinkedList<DataTypeId> selectorIds = new LinkedList<>();
		for (final String selectorType : getSelectorTypes()) {
			try {
				final DataTypeId selectorId = new DataTypeId(selectorType, calculateUid(selectorType, selectorString));
				if (selectorId != null) {
					selectorIds.add(selectorId);
				}
			} catch (final Throwable t) {
				// Ignored
			}
		}
		return selectorIds;
	}

	@Override
	@JsonIgnore
	public String calculateUid(String type, String simpleRep) {
		return simpleRepParsers.parse(type, simpleRep)
			.getId().uid;
	}
}