package net.thomas.portfolio.shared_objects.analytics;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriorKnowledge {
	@JsonIgnore
	public String alias;
	@JsonIgnore
	public RecognitionLevel recognition;
	@JsonIgnore
	public RecognitionLevel isDanish;

	public PriorKnowledge() {
	}

	public PriorKnowledge(String alias, RecognitionLevel recognition, RecognitionLevel isDanish) {
		this.alias = alias;
		this.recognition = recognition;
		this.isDanish = isDanish;
	}

	public String getPk_alias() {
		return alias;
	}

	public void setPk_alias(String alias) {
		this.alias = alias;
	}

	public RecognitionLevel getPk_recognition() {
		return recognition;
	}

	public void setPk_recognition(RecognitionLevel recognition) {
		this.recognition = recognition;
	}

	public RecognitionLevel getPk_isDanish() {
		return isDanish;
	}

	public void setPk_isDanish(RecognitionLevel isDanish) {
		this.isDanish = isDanish;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PriorKnowledge) {
			final PriorKnowledge other = (PriorKnowledge) obj;
			return alias.equals(other.alias) && recognition == other.recognition && isDanish == other.isDanish;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
