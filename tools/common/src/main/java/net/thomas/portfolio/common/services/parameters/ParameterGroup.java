package net.thomas.portfolio.common.services.parameters;

import static java.util.stream.Collectors.toList;

import java.util.Collection;

public interface ParameterGroup {
	Parameter[] getParameters();

	public static class SimpleParameterGroup implements ParameterGroup {
		private final Parameter[] parameters;

		public static ParameterGroup asGroup(Parameter... parameters) {
			return new SimpleParameterGroup(parameters);
		}

		public SimpleParameterGroup(Parameter... parameters) {
			this.parameters = parameters;
		}

		@Override
		public Parameter[] getParameters() {
			return parameters;
		}
	}

	public static class CollectionParameterGroup implements ParameterGroup {
		private final String name;
		private final Collection<? extends Object> values;

		public CollectionParameterGroup(String name, Collection<? extends Object> values) {
			this.name = name;
			this.values = values;
		}

		@Override
		public Parameter[] getParameters() {
			final Collection<Parameter> parameters = values.stream()
				.map(value -> new PreSerializedParameter(name, value))
				.collect(toList());
			return parameters.toArray(new Parameter[parameters.size()]);
		}
	}
}