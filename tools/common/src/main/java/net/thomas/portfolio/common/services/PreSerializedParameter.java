package net.thomas.portfolio.common.services;

public class PreSerializedParameter implements Parameter {
	private final String name;
	private final Object[] values;

	public PreSerializedParameter(String name, Object... values) {
		this.name = name;
		this.values = values;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object[] getValues() {
		return values;
	}
}