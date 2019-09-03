package net.thomas.portfolio.common.services.parameters;

public interface Parameter {
	String getName();

	boolean hasValue();

	Object getValue();
}
