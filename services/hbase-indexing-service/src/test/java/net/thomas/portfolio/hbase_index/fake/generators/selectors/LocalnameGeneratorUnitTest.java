package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.selectors.Localname;

public class LocalnameGeneratorUnitTest {

	private static final long SOME_RANDOM_SEED = 1l;
	private static Iterator<Localname> GENERATOR;

	@BeforeClass
	public static void setupGenerator() {
		GENERATOR = new LocalnameGenerator(SOME_RANDOM_SEED).iterator();
	}

	@Test
	public void shouldBeAtLeastThreeCharactorsInName() {
		final Localname localname = GENERATOR.next();
		assertTrue(localname.name.length() >= 3);
	}

	@Test
	public void shouldBeAtMostFifteenCharactorsInName() {
		final Localname localname = GENERATOR.next();
		assertTrue(localname.name.length() <= 15);
	}

	@Test
	public void shouldHaveDifferentNames() {
		final Localname localname1 = GENERATOR.next();
		final Localname localname2 = GENERATOR.next();
		assertNotEquals(localname1.name, localname2.name);
	}

	@Test
	public void shouldNotContainAnyWhitespaces() {
		for (int i = 0; i < 100; i++) {
			final Localname localname = GENERATOR.next();
			if (localname.name.contains(" ")) {
				throw new RuntimeException("Found whitespaces in sample");
			}
		}
	}
}
