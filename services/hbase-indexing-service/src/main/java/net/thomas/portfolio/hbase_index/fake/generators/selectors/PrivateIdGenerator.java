package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import net.thomas.portfolio.hbase_index.fake.generators.SelectorGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.DigitsGenerator;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;

public class PrivateIdGenerator extends SelectorGenerator<PrivateId> {

	private final DigitsGenerator generator;

	public PrivateIdGenerator(long randomSeed) {
		super(randomSeed);
		generator = new DigitsGenerator(15, 15, randomSeed);
	}

	@Override
	protected PrivateId createInstance() {
		return new PrivateId(generator.generate());
	}
}