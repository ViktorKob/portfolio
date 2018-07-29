package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import net.thomas.portfolio.hbase_index.fake.generators.SelectorGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.StringGenerator;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;

public class DisplayedNameGenerator extends SelectorGenerator<DisplayedName> {

	private final StringGenerator generator;

	public DisplayedNameGenerator(long randomSeed) {
		super(randomSeed);
		generator = new StringGenerator(3, 15, 0.15, random.nextLong());
	}

	@Override
	protected DisplayedName createInstance() {
		return new DisplayedName(generator.generate());
	}
}
