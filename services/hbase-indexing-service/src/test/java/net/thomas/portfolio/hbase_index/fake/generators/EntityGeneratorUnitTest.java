package net.thomas.portfolio.hbase_index.fake.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.Entity;

public class EntityGeneratorUnitTest {

	private static final long SOME_RANDOM_SEED = 1l;
	private static Iterator<Entity> GENERATOR;

	@BeforeClass
	public static void setupGenerator() {
		GENERATOR = new FakeEntityGenerator(SOME_RANDOM_SEED).iterator();
	}

	@Test
	public void shouldHaveUid() {
		final Entity entity = GENERATOR.next();
		assertNotNull(entity.uid);
	}

	@Test
	public void shouldHaveSameUids() {
		final Entity entity1 = GENERATOR.next();
		final Entity entity2 = GENERATOR.next();
		assertEquals(entity1.uid, entity2.uid);
	}

	static class FakeEntityGenerator extends EntityGenerator<Entity> {
		public FakeEntityGenerator(long randomSeed) {
			super(false, randomSeed);
		}

		@Override
		protected Entity createInstance() {
			return new Entity();
		}
	}
}
