package net.thomas.portfolio.shared_objects.document_summaries;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BtypeDocumentSummary extends DocumentSummary {
	private String btypeDocumentExtraField;

	public BtypeDocumentSummary() {}

	public String getBtypeDocumentExtraField() {
		return btypeDocumentExtraField;
	}

	public void setBtypeDocumentExtraField(String btypeDocumentExtraField) {
		this.btypeDocumentExtraField = btypeDocumentExtraField;
	}

	@Override
	public String toString() {
		return "BtypeDocument: " + getTimeOfCreation() + ", " + getHeadline() + ", " + getBtypeDocumentExtraField();
	}
}
