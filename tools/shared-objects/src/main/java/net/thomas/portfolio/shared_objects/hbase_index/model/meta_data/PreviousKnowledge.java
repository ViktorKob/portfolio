package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PreviousKnowledge {
	public RecognitionLevel recognition;
	public RecognitionLevel isDanish;

	public PreviousKnowledge() {
	}

	public PreviousKnowledge(RecognitionLevel recognition, RecognitionLevel isDanish) {
		this.recognition = recognition;
		this.isDanish = isDanish;
	}

	public RecognitionLevel getRecognition() {
		return recognition;
	}

	public void setRecognition(RecognitionLevel recognition) {
		this.recognition = recognition;
	}

	public RecognitionLevel getIsDanish() {
		return isDanish;
	}

	public void setIsDanish(RecognitionLevel isDanish) {
		this.isDanish = isDanish;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
