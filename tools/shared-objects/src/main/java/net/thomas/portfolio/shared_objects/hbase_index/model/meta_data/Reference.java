package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import java.util.Set;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Reference {
	public Source source;
	public Set<Classification> classifications;
	public String originalId;

	public Reference(Source source, Set<Classification> classifications, String originalId) {
		this.source = source;
		this.classifications = classifications;
		this.originalId = originalId;
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
		return ReflectionToStringBuilder.toString(this);
	}
}