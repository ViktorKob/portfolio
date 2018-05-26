package net.thomas.portfolio.shared_objects.serialization_experiment.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AtypeDocument extends Document {
	private String atypeDocumentExtraField;

	public AtypeDocument() {}

	public String getAtypeDocumentExtraField() {
		return atypeDocumentExtraField;
	}

	public void setAtypeDocumentExtraField(String atypeDocumentExtraField) {
		this.atypeDocumentExtraField = atypeDocumentExtraField;
	}

	@Override
	public String toString() {
		return "AtypeDocument: " + getTimeOfCreation() + ", " + getHeadline() + ", " + getAtypeDocumentExtraField();
	}
}
