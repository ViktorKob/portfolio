package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;

public class DisplayedNameGeneratorUnitTest {

	private static final long SOME_RANDOM_SEED = 1l;
	private static Iterator<DisplayedName> GENERATOR;

	@BeforeClass
	public static void setupGenerator() {
		GENERATOR = new DisplayedNameGenerator(SOME_RANDOM_SEED).iterator();
	}

	@Test
	public void shouldBeAtLeastThreeCharactorsInName() {
		final DisplayedName displayedName = GENERATOR.next();
		assertTrue(displayedName.name.length() >= 3);
	}

	@Test
	public void shouldBeAtMostFifteenCharactorsInName() {
		final DisplayedName displayedName = GENERATOR.next();
		assertTrue(displayedName.name.length() <= 15);
	}

	@Test
	public void shouldBeWhitespaceInAtLeastSomeOfThem() {
		for (int i = 0; i < 100; i++) {
			final DisplayedName displayedName = GENERATOR.next();
			if (displayedName.name.contains(" ")) {
				return;
			}
		}
		throw new RuntimeException("Unable to locate any whitespaces in samples");
	}

	@Test
	public void shouldHaveDifferentNames() {
		final DisplayedName displayedName1 = GENERATOR.next();
		final DisplayedName displayedName2 = GENERATOR.next();
		assertNotEquals(displayedName1.name, displayedName2.name);
	}
}
