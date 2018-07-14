package net.thomas.portfolio.shared_objects.hbase_index.schema;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParserLibrarySerializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HbaseIndexSchemaImpl implements HbaseIndexSchema {

	protected Map<String, Fields> dataTypeFields;
	protected Set<String> dataTypes;
	protected Set<String> documentTypes;
	protected Set<String> selectorTypes;
	protected Set<String> simpleRepresentableTypes;
	protected Map<String, Collection<Indexable>> indexables;
	protected SimpleRepresentationParserLibrarySerializable simpleRepParsers;
	@JsonIgnore
	protected Map<String, Set<String>> indexableDocumentTypes;
	@JsonIgnore
	protected Map<String, Set<String>> indexableRelations;
	@JsonIgnore
	protected Set<String> allIndexableRelations;

	public HbaseIndexSchemaImpl() {
	}

	public void setDataTypeFields(Map<String, Fields> dataTypeFields) {
		this.dataTypeFields = dataTypeFields;
		dataTypes = dataTypeFields.keySet();
	}

	public void setDocumentTypes(Set<String> documentTypes) {
		this.documentTypes = documentTypes;
	}

	public void setSelectorTypes(Set<String> selectorTypes) {
		this.selectorTypes = selectorTypes;
	}

	public void setSimpleRepresentableTypes(Set<String> simpleRepresentableTypes) {
		this.simpleRepresentableTypes = simpleRepresentableTypes;
	}

	public void setIndexables(Map<String, Collection<Indexable>> indexables) {
		this.indexables = indexables;
		indexableDocumentTypes = buildIndexableMap(indexables, Indexable::getDocumentType);
		indexableRelations = buildIndexableMap(indexables, Indexable::getPath);
		allIndexableRelations = indexableRelations.values()
			.stream()
			.flatMap(Collection::stream)
			.collect(toSet());
	}

	private Map<String, Set<String>> buildIndexableMap(Map<String, Collection<Indexable>> indexables, Function<? super Indexable, ? extends String> mapper) {
		final Map<String, Set<String>> relationMap = new HashMap<>();
		for (final String selectorType : selectorTypes) {
			if (indexables.containsKey(selectorType)) {
				final Collection<Indexable> selectorIndexables = indexables.get(selectorType);
				relationMap.put(selectorType, selectorIndexables.stream()
					.map(mapper)
					.collect(toSet()));
			}
		}
		return relationMap;
	}

	public void setSimpleRepParsers(SimpleRepresentationParserLibrarySerializable simpleRepParsers) {
		this.simpleRepParsers = simpleRepParsers;
	}

	public Map<String, Set<String>> getIndexableDocumentTypes() {
		return indexableDocumentTypes;
	}

	public Map<String, Fields> getDataTypeFields() {
		return dataTypeFields;
	}

	@Override
	public Fields getFieldsForDataType(String dataType) {
		return dataTypeFields.get(dataType);
	}

	@Override
	public Set<String> getSimpleRepresentableTypes() {
		return simpleRepresentableTypes;
	}

	public Map<String, Collection<Indexable>> getIndexables() {
		return indexables;
	}

	@Override
	public Set<String> getDocumentTypes() {
		return documentTypes;
	}

	@Override
	public Set<String> getSelectorTypes() {
		return selectorTypes;
	}

	@Override
	@JsonIgnore
	public Set<String> getDataTypes() {
		return dataTypes;
	}

	@Override
	@JsonIgnore
	public Set<String> getIndexableDocumentTypes(String selectorType) {
		return indexableDocumentTypes.get(selectorType);
	}

	public Map<String, Set<String>> getIndexableRelations() {
		return indexableRelations;
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

	@Override
	public Collection<Indexable> getIndexables(String selectorType) {
		return indexables.get(selectorType);
	}

	public SimpleRepresentationParserLibrarySerializable getSimpleRepParsers() {
		return simpleRepParsers;
	}

	@Override
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