package net.model.meta_data;

import java.util.Set;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Reference {
	public final Source source;
	public final Set<Classification> classifications;
	public final String originalId;

	public Reference(Source source, Set<Classification> classifications, String originalId) {
		this.source = source;
		this.classifications = classifications;
		this.originalId = originalId;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}