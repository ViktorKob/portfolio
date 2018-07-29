package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import net.thomas.portfolio.hbase_index.fake.generators.SelectorGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.DigitsGenerator;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;

public class PublicIdGenerator extends SelectorGenerator<PublicId> {

	private final DigitsGenerator generator;

	public PublicIdGenerator(long randomSeed) {
		super(randomSeed);
		generator = new DigitsGenerator(6, 14, randomSeed);
	}

	@Override
	protected PublicId createInstance() {
		return new PublicId(generator.generate());
	}
}
