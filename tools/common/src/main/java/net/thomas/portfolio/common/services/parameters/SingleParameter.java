package net.thomas.portfolio.common.services.parameters;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

public class SingleParameter implements Parameter {
	private final String name;
	private final Object value;

	public SingleParameter(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasValue() {
		return value != null && !value.toString().isEmpty();
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}