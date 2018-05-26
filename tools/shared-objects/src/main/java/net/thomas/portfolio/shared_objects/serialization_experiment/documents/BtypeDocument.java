package net.thomas.portfolio.shared_objects.serialization_experiment.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BtypeDocument extends Document {
	private String btypeDocumentExtraField;

	public BtypeDocument() {}

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
