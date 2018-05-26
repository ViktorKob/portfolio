package net.thomas.portfolio.shared_objects.serialization_experiment.document_summaries;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(AtypeDocumentSummary.class), @Type(BtypeDocumentSummary.class) })
public abstract class DocumentSummary {

	private long timeOfCreation;
	private String headline;

	public DocumentSummary() {}

	public long getTimeOfCreation() {
		return timeOfCreation;
	}

	public void setTimeOfCreation(long timeOfCreation) {
		this.timeOfCreation = timeOfCreation;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	@Override
	public String toString() {
		return "Document: " + headline;
	}
}
