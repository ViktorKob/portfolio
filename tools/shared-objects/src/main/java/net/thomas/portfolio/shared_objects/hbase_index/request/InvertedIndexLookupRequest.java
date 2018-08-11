package net.thomas.portfolio.shared_objects.hbase_index.request;

import static net.thomas.portfolio.common.services.parameters.ParameterGroup.asGroup;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.common.services.parameters.ParameterGroup;
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

	public Collection<String> getDocumentTypes() {
		return documentTypes;
	}

	public void setDocumentTypes(Set<String> documentTypes) {
		this.documentTypes = documentTypes;
	}

	public Collection<String> getRelations() {
		return relations;
	}

	public void setRelations(Set<String> relations) {
		this.relations = relations;
	}

	@JsonIgnore
	public ParameterGroup[] getGroups() {
		return new ParameterGroup[] { legalInfo, bounds, asGroup("documentType", documentTypes), asGroup("relation", relations) };
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bounds == null ? 0 : bounds.hashCode());
		result = prime * result + (documentTypes == null ? 0 : documentTypes.hashCode());
		result = prime * result + (legalInfo == null ? 0 : legalInfo.hashCode());
		result = prime * result + (relations == null ? 0 : relations.hashCode());
		result = prime * result + (selectorId == null ? 0 : selectorId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof InvertedIndexLookupRequest)) {
			return false;
		}
		final InvertedIndexLookupRequest other = (InvertedIndexLookupRequest) obj;
		if (bounds == null) {
			if (other.bounds != null) {
				return false;
			}
		} else if (!bounds.equals(other.bounds)) {
			return false;
		}
		if (documentTypes == null) {
			if (other.documentTypes != null) {
				return false;
			}
		} else if (!documentTypes.equals(other.documentTypes)) {
			return false;
		}
		if (legalInfo == null) {
			if (other.legalInfo != null) {
				return false;
			}
		} else if (!legalInfo.equals(other.legalInfo)) {
			return false;
		}
		if (relations == null) {
			if (other.relations != null) {
				return false;
			}
		} else if (!relations.equals(other.relations)) {
			return false;
		}
		if (selectorId == null) {
			if (other.selectorId != null) {
				return false;
			}
		} else if (!selectorId.equals(other.selectorId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}
