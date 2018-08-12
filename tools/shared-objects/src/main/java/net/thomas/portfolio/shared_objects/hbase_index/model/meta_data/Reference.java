package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import java.util.Set;

public class Reference {
	private Source source;
	private String originalId;
	private Set<Classification> classifications;

	public Reference() {
	}

	public Reference(Source source, String originalId, Set<Classification> classifications) {
		this.source = source;
		this.originalId = originalId;
		this.classifications = classifications;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Set<Classification> getClassifications() {
		return classifications;
	}

	public void setClassifications(Set<Classification> classifications) {
		this.classifications = classifications;
	}

	public String getOriginalId() {
		return originalId;
	}

	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}