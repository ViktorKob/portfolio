package net.thomas.portfolio.shared_objects.hbase_index.request;

import static net.thomas.portfolio.common.services.parameters.ParameterGroup.asGroup;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class InvertedIndexLookupRequest {
	public DataTypeId selectorId;
	public LegalInformation legalInfo;
	public Bounds bounds;
	@ApiModelProperty(value = "The relation types to include in the response (leaving it out will return documents of all types)")
	public Set<String> documentTypes;
	@ApiModelProperty(value = "The relation types to include in the response (leaving it out will return documents with all types of relations)")
	public Set<String> relations;

	public InvertedIndexLookupRequest() {
	}

	public InvertedIndexLookupRequest(final DataTypeId selectorId, final LegalInformation legalInfo, final Bounds bounds, final Set<String> documentTypes,
			final Set<String> relations) {
		this.selectorId = selectorId;
		this.legalInfo = legalInfo;
		this.bounds = bounds;
		this.documentTypes = documentTypes;
		this.relations = relations;
	}

	public InvertedIndexLookupRequest(final InvertedIndexLookupRequest source) {
		selectorId = new DataTypeId(source.selectorId);
		legalInfo = new LegalInformation(source.legalInfo);
		bounds = new Bounds(source.bounds);
		documentTypes = new HashSet<>(source.documentTypes);
		relations = new HashSet<>(source.relations);
	}

	public DataTypeId getSelectorId() {
		return selectorId;
	}

	public void setSelectorId(final DataTypeId selectorId) {
		this.selectorId = selectorId;
	}

	public LegalInformation getLegalInfo() {
		return legalInfo;
	}

	public void setLegalInfo(final LegalInformation legalInfo) {
		this.legalInfo = legalInfo;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(final Bounds bounds) {
		this.bounds = bounds;
	}

	public Collection<String> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(final Set<String> documentTypes) {
		this.documentTypes = documentTypes;
	}

	public Collection<String> getRelations() {
		return relations;
	}

	public void setRelations(final Set<String> relations) {
		this.relations = relations;
	}

	@JsonIgnore
	@ApiModelProperty(hidden = true)
	public ParameterGroup[] getGroups() {
		return new ParameterGroup[] { legalInfo, bounds, asGroup("documentType", documentTypes), asGroup("relation", relations) };
	}

	@Override
	public String toString() {
		return asString(this);
	}
}
