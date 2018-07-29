package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;

public class PublicIdGeneratorUnitTest {

	private static final long SOME_RANDOM_SEED = 1l;
	private static Iterator<PublicId> GENERATOR;

	@BeforeClass
	public static void setupGenerator() {
		GENERATOR = new PublicIdGenerator(SOME_RANDOM_SEED).iterator();
	}

	@Test
	public void shouldBeAtLeastSixCharactorsInNumbers() {
		final PublicId publicId = GENERATOR.next();
		assertTrue(publicId.number.length() >= 6);
	}

	@Test
	public void shouldBeAtMostFourteenCharactorsInNumber() {
		final PublicId publicId = GENERATOR.next();
		assertTrue(publicId.number.length() <= 14);
	}

	@Test
	public void shouldHaveDifferentNumbers() {
		final PublicId publicId1 = GENERATOR.next();
		final PublicId publicId2 = GENERATOR.next();
		assertNotEquals(publicId1.number, publicId2.number);
	}

	@Test
	public void shouldNotContainAnyWhitespaces() {
		for (int i = 0; i < 100; i++) {
			final PublicId publicId = GENERATOR.next();
			if (publicId.number.contains(" ")) {
				throw new RuntimeException("Found whitespaces in sample");
			}
		}
	}
}
