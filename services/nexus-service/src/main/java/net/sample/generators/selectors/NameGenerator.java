package net.sample.generators.selectors;

import net.model.DataType;
import net.sample.generators.SelectorGenerator;
import net.sample.generators.primitives.StringGenerator;

public class NameGenerator extends SelectorGenerator {

	private final StringGenerator generator;
	private final String field;

	public NameGenerator(String type, String field, int minLength, int maxLength, double whiteSpacePropability, long randomSeed) {
		super(type, randomSeed);
		this.field = field;
		generator = new StringGenerator(minLength, maxLength, whiteSpacePropability, random.nextLong());
	}

	@Override
	protected boolean keyShouldBeUnique() {
		return false;
	}

	@Override
	protected void populateValues(DataType sample) {
		sample.put(field, generator.generate());
	}
}
