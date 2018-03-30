package net.sample.generators.selectors;

import net.model.DataType;
import net.sample.generators.SelectorGenerator;
import net.sample.generators.primitives.DigitsGenerator;

public class NumberGenerator extends SelectorGenerator {

	private final DigitsGenerator generator;

	public NumberGenerator(String type, int minLength, int maxLength, long randomSeed) {
		super(type, randomSeed);
		generator = new DigitsGenerator(minLength, maxLength, random.nextLong());
	}

	@Override
	protected boolean keyShouldBeUnique() {
		return false;
	}

	@Override
	protected void populateValues(DataType sample) {
		sample.put("number", generator.generate());
	}
}
