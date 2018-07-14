package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import net.thomas.portfolio.hbase_index.fake.generators.SelectorGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.StringGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class NameGenerator extends SelectorGenerator {

	private final StringGenerator generator;
	private final String field;

	public NameGenerator(String type, String field, int minLength, int maxLength, double whiteSpacePropability, HbaseIndexSchema schema, long randomSeed) {
		super(type, schema, randomSeed);
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
