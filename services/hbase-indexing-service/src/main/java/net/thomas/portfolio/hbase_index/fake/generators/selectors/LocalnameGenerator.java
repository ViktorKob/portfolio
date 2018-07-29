package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import net.thomas.portfolio.hbase_index.fake.generators.SelectorGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.StringGenerator;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;

public class LocalnameGenerator extends SelectorGenerator<Localname> {

	private final StringGenerator generator;

	public LocalnameGenerator(long randomSeed) {
		super(randomSeed);
		generator = new StringGenerator(3, 15, 0.0, random.nextLong());
	}

	@Override
	protected Localname createInstance() {
		return new Localname(generator.generate());
	}
}
