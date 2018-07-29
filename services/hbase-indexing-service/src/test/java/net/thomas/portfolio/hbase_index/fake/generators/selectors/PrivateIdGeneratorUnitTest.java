package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;

public class PrivateIdGeneratorUnitTest {

	private static final long SOME_RANDOM_SEED = 1l;
	private static Iterator<PrivateId> GENERATOR;

	@BeforeClass
	public static void setupGenerator() {
		GENERATOR = new PrivateIdGenerator(SOME_RANDOM_SEED).iterator();
	}

	@Test
	public void shouldBeExactlyFifteenCharactorsInNumber() {
		final PrivateId privateId = GENERATOR.next();
		assertEquals(privateId.number.length(), 15);
	}

	@Test
	public void shouldHaveDifferentNumbers() {
		final PrivateId privateId1 = GENERATOR.next();
		final PrivateId privateId2 = GENERATOR.next();
		assertNotEquals(privateId1.number, privateId2.number);
	}

	@Test
	public void shouldNotContainAnyWhitespaces() {
		for (int i = 0; i < 100; i++) {
			final PrivateId privateId = GENERATOR.next();
			if (privateId.number.contains(" ")) {
				throw new RuntimeException("Found whitespaces in sample");
			}
		}
	}
}
