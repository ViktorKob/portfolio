package net.thomas.portfolio.hbase_index.fake.generators;

import net.thomas.portfolio.hbase_index.schema.meta.MetaEntity;

public abstract class MetaEntityGenerator<TYPE extends MetaEntity> extends EntityGenerator<TYPE> {

	public MetaEntityGenerator(long randomSeed) {
		super(true, randomSeed);
	}
}