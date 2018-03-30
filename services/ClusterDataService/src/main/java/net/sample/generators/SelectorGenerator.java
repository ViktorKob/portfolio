package net.sample.generators;

import net.model.DataType;
import net.model.types.Selector;

public abstract class SelectorGenerator extends DataTypeGenerator {

	public SelectorGenerator(String dataTypeName, long randomSeed) {
		super(dataTypeName, false, randomSeed);
	}

	@Override
	public DataType next() {
		final Selector sample = new Selector(dataTypeName);
		populateFields(sample);
		return sample;
	}
}
