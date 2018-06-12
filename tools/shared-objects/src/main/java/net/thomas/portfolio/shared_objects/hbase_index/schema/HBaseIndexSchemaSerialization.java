package net.thomas.portfolio.shared_objects.hbase_index.schema;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.LinkedHashMap;
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

	protected Map<String, LinkedHashMap<String, Field>> dataTypeFields;
	protected Set<String> dataTypes;
	protected Set<String> documentTypes;
	protected Set<String> selectorTypes;
	protected Set<String> simpleRepresentableTypes;
	protected Map<String, Collection<Indexable>> indexables;
	protected Map<String, Set<String>> indexableDocumentTypes;
	protected Map<String, Set<String>> indexableRelations;
	@JsonIgnore
	protected Set<String> allIndexableRelations;

	public HBaseIndexSchemaSerialization() {
	}

	public void initialize() {
		simpleRepParsers = new SimpleRepresentationParserLibrary(this);
	}

	public Map<String, LinkedHashMap<String, Field>> getDataTypeFields() {
		return dataTypeFields;
	}

	public void setDataTypeFields(Map<String, LinkedHashMap<String, Field>> dataTypeFields) {
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
	public List<Field> getFieldsForDataType(String dataType) {
		return new LinkedList<>(dataTypeFields.get(dataType)
			.values());
	}

	@Override
	public Set<String> getSimpleRepresentableTypes() {
		return simpleRepresentableTypes;
	}

	public void setSimpleRepresentableTypes(Set<String> simpleRepresentableTypes) {
		this.simpleRepresentableTypes = simpleRepresentableTypes;
	}

	public Map<String, Set<String>> getIndexableDocumentTypes() {
		return indexableDocumentTypes;
	}

	public void setIndexableDocumentTypes(Map<String, Set<String>> indexableDocumentTypes) {
		this.indexableDocumentTypes = indexableDocumentTypes;
	}

	@Override
	@JsonIgnore
	public Set<String> getIndexableDocumentTypes(String selectorType) {
		return indexableDocumentTypes.get(selectorType);
	}

	public Map<String, Set<String>> getIndexableRelations() {
		return indexableRelations;
	}

	public void setIndexableRelations(Map<String, Set<String>> indexableRelations) {
		this.indexableRelations = indexableRelations;
		allIndexableRelations = indexableRelations.values()
			.stream()
			.flatMap(Collection::stream)
			.collect(toSet());
	}

	@Override
	@JsonIgnore
	public Set<String> getIndexableRelations(String selectorType) {
		return indexableRelations.get(selectorType);
	}

	@Override
	public Set<String> getAllIndexableRelations() {
		return allIndexableRelations;
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