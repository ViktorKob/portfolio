package net.thomas.portfolio.shared_objects.serialization_experiment.documents;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(AtypeDocument.class), @Type(BtypeDocument.class) })
public abstract class Document {

	private long timeOfCreation;
	private String headline;

	public Document() {}

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
