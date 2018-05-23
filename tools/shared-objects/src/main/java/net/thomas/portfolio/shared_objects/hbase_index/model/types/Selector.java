package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class Selector extends DataType {
	private static final long serialVersionUID = 1L;

	private long lowerBound;
	private long upperBound;
	private String justification;

	public Selector(String type) {
		super(type);
	}

	public long getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(long lowerBound) {
		this.lowerBound = lowerBound;
	}

	public long getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(long upperBound) {
		this.upperBound = upperBound;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}