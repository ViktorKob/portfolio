package net.thomas.portfolio.shared_objects.hbase_index.request;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.common.services.ParameterGroup;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvertedIndexLookupRequest {
	public DataTypeId selectorId;
	public LegalInformation legalInfo;
	public Bounds bounds;
	public Set<String> documentTypes;
	public Set<String> relations;

	public InvertedIndexLookupRequest() {
	}

	public InvertedIndexLookupRequest(DataTypeId selectorId, LegalInformation legalInfo, Bounds bounds, Set<String> documentTypes, Set<String> relations) {
		this.selectorId = selectorId;
		this.legalInfo = legalInfo;
		this.bounds = bounds;
		this.documentTypes = documentTypes;
		this.relations = relations;
	}

	public InvertedIndexLookupRequest(InvertedIndexLookupRequest source) {
		selectorId = new DataTypeId(source.selectorId);
		legalInfo = new LegalInformation(source.legalInfo);
		bounds = new Bounds(source.bounds);
		documentTypes = new HashSet<>(source.documentTypes);
		relations = new HashSet<>(source.relations);
	}

	public DataTypeId getSelectorId() {
		return selectorId;
	}

	public void setSelectorId(DataTypeId selectorId) {
		this.selectorId = selectorId;
	}

	public LegalInformation getLegalInfo() {
		return legalInfo;
	}

	public void setLegalInfo(LegalInformation legalInfo) {
		this.legalInfo = legalInfo;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public Set<String> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(Set<String> documentTypes) {
		this.documentTypes = documentTypes;
	}

	public Set<String> getRelations() {
		return relations;
	}

	public void setRelations(Set<String> relations) {
		this.relations = relations;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InvertedIndexLookupRequest) {
			final InvertedIndexLookupRequest other = (InvertedIndexLookupRequest) obj;
			return selectorId.equals(other.selectorId) && legalInfo.equals(other.legalInfo) && bounds.equals(other.bounds)
					&& documentTypes.equals(other.documentTypes) && relations.equals(other.relations);
		}
		return false;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public ParameterGroup[] getGroups() {
		return new ParameterGroup[] { selectorId, legalInfo, bounds, new ParameterGroup.CollectionParameterGroup("documentType", documentTypes),
				new ParameterGroup.CollectionParameterGroup("relation", relations) };
	}
}
