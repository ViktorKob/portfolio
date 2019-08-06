package net.thomas.portfolio.shared_objects.hbase_index.schema;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "HBASE Index Schema", description = "Schema for the data model in the HBASE index")
public class HbaseIndexSchemaImpl implements HbaseIndexSchema {

	@ApiModelProperty("The fields available in each data type")
	protected Map<String, Fields> dataTypeFields;
	@ApiModelProperty("Names of all the valid data types in the index")
	protected Set<String> dataTypes;
	@ApiModelProperty("Names of all the valid document types in the index")
	protected Set<String> documentTypes;
	@ApiModelProperty("Names of all the valid selector types in the index")
	protected Set<String> selectorTypes;
	@ApiModelProperty("Names of all selector types in the index that have a simple string representation")
	protected Set<String> simpleRepresentableTypes;
	@ApiModelProperty("The indexables for each data type")
	protected Map<String, Collection<Indexable>> indexables;
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
		allIndexableRelations = indexableRelations.values().stream().flatMap(Collection::stream).collect(toSet());
	}

	private Map<String, Set<String>> buildIndexableMap(Map<String, Collection<Indexable>> indexables, Function<? super Indexable, ? extends String> mapper) {
		final Map<String, Set<String>> relationMap = new HashMap<>();
		for (final String selectorType : selectorTypes) {
			if (indexables.containsKey(selectorType)) {
				final Collection<Indexable> selectorIndexables = indexables.get(selectorType);
				relationMap.put(selectorType, selectorIndexables.stream().map(mapper).collect(toSet()));
			}
		}
		return relationMap;
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

	@Override
	@JsonIgnore
	public Field getFieldForIndexable(Indexable indexable) {
		return dataTypeFields.get(indexable.documentType).get(indexable.documentField);
	}
}