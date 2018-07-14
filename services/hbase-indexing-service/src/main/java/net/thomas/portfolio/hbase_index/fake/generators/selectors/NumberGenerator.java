package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import net.thomas.portfolio.hbase_index.fake.generators.SelectorGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.DigitsGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class NumberGenerator extends SelectorGenerator {

	private final DigitsGenerator generator;

	public NumberGenerator(String type, int minLength, int maxLength, HbaseIndexSchema schema, long randomSeed) {
		super(type, schema, randomSeed);
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
