package net.thomas.portfolio.hbase_index.fake.generators;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.UidCalculator;

public abstract class EntityGenerator<TYPE extends Entity> implements Iterable<TYPE>, Iterator<TYPE> {

	protected final Random random;
	protected final UidCalculator idTool;

	public EntityGenerator(boolean keyShouldBeUnique, long randomSeed) {
		idTool = new UidCalculator(keyShouldBeUnique);
		random = new Random(randomSeed);
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public TYPE next() {
		final TYPE entity = createInstance();
		entity.uid = idTool.calculate(entity);
		return entity;
	}

	protected abstract TYPE createInstance();

	@Override
	public Iterator<TYPE> iterator() {
		return this;
	}

	protected <T> T randomSample(List<T> values) {
		return values.get(random.nextInt(values.size()));
	}

	protected <T> T randomProgressiveSample(List<T> values) {
		for (int i = 0; i < values.size() - 1; i++) {
			if (random.nextDouble() < 0.5d) {
				return values.get(i);
			}
		}
		return values.get(values.size() - 1);
	}

}
