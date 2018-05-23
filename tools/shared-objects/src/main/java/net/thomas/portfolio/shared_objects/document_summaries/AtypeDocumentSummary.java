package net.thomas.portfolio.shared_objects.document_summaries;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AtypeDocumentSummary extends DocumentSummary {
	private String atypeDocumentExtraField;

	public AtypeDocumentSummary() {}

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
