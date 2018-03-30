package net.model.meta_data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class PreviousKnowledge {
	public final RecognitionLevel recognition;
	public final RecognitionLevel isDanish;

	public PreviousKnowledge(RecognitionLevel recognition, RecognitionLevel isDanish) {
		this.recognition = recognition;
		this.isDanish = isDanish;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
