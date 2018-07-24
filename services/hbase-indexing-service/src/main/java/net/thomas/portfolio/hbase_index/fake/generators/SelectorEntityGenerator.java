package net.thomas.portfolio.hbase_index.fake.generators;

import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;

public abstract class SelectorEntityGenerator<TYPE extends SelectorEntity> extends EntityGenerator<TYPE> {

	public SelectorEntityGenerator(long randomSeed) {
		super(false, randomSeed);
	}
}
