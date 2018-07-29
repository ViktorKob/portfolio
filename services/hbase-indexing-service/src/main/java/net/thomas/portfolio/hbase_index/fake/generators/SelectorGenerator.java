package net.thomas.portfolio.hbase_index.fake.generators;

import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;

public abstract class SelectorGenerator<TYPE extends SelectorEntity> extends EntityGenerator<TYPE> {

	public SelectorGenerator(long randomSeed) {
		super(false, randomSeed);
	}
}
