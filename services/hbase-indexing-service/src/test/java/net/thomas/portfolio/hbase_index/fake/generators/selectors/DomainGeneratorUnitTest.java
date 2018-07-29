package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.selectors.Domain;

public class DomainGeneratorUnitTest {

	private static final long SOME_RANDOM_SEED = 1l;
	private static final int MINIMUM_CHARACTORS = 3;
	private static final int MAXIMUM_CHARACTORS = 6;
	private static Iterator<Domain> DOMAIN_PART_ONLY_GENERATOR;
	private static Iterator<Domain> RECURSIVE_DOMAIN_GENERATOR;

	@BeforeClass
	public static void setupGenerator() {
		DOMAIN_PART_ONLY_GENERATOR = new DomainGenerator(emptySet(), MINIMUM_CHARACTORS, MAXIMUM_CHARACTORS, SOME_RANDOM_SEED).iterator();
		RECURSIVE_DOMAIN_GENERATOR = new DomainGenerator(singleton(DOMAIN_PART_ONLY_GENERATOR.next()), MINIMUM_CHARACTORS, MAXIMUM_CHARACTORS,
				SOME_RANDOM_SEED).iterator();
	}

	@Test
	public void shouldBeAtLeastThreeCharactorsInName() {
		final Domain domain = DOMAIN_PART_ONLY_GENERATOR.next();
		assertTrue(domain.domainPart.length() >= MINIMUM_CHARACTORS);
	}

	@Test
	public void shouldBeAtMostFifteenCharactorsInName() {
		final Domain domain = DOMAIN_PART_ONLY_GENERATOR.next();
		assertTrue(domain.domainPart.length() <= MAXIMUM_CHARACTORS);
	}

	@Test
	public void shouldHaveDifferentNames() {
		final Domain domain1 = DOMAIN_PART_ONLY_GENERATOR.next();
		final Domain domain2 = DOMAIN_PART_ONLY_GENERATOR.next();
		assertNotEquals(domain1.domainPart, domain2.domainPart);
	}

	@Test
	public void shouldNotContainAnyWhitespaces() {
		for (int i = 0; i < 100; i++) {
			final Domain domain = DOMAIN_PART_ONLY_GENERATOR.next();
			if (domain.domainPart.contains(" ")) {
				throw new RuntimeException("Found whitespaces in sample");
			}
		}
	}

	@Test
	public void shouldNotPickParentDomainWhenNotAvailable() {
		final Domain domain = DOMAIN_PART_ONLY_GENERATOR.next();
		assertNull(domain.domain);
	}

	@Test
	public void shouldPickParentDomainWhenPossible() {
		final Domain domain = RECURSIVE_DOMAIN_GENERATOR.next();
		assertNotNull(domain.domain);
	}
}