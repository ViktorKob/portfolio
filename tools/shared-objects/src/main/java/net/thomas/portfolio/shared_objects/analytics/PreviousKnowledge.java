package net.thomas.portfolio.shared_objects.analytics;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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
