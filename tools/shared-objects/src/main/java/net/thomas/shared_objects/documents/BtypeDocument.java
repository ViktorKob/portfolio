package net.thomas.shared_objects.documents;

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
