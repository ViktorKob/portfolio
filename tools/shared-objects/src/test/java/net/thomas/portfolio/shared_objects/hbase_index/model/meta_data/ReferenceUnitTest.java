package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Classification.BLUE;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Source.LEMON;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertToStringIsValid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class ReferenceUnitTest {
	private Reference reference;

	@Before
	public void setUpForTest() {
		reference = new Reference();
	}

	@Test
	public void shouldContainSourceAfterAddition() {
		reference.setSource(LEMON);
		assertEquals(LEMON, reference.getSource());
	}

	@Test
	public void shouldContainOriginalIdAfterAddition() {
		reference.setOriginalId(SOME_ID);
		assertEquals(SOME_ID, reference.getOriginalId());
	}

	@Test
	public void shouldContainClassificationsAfterAddition() {
		reference.setClassifications(singleton(BLUE));
		assertEquals(singleton(BLUE), reference.getClassifications());
	}

	@Test
	public void shouldContainAllClassifications() {
		reference.setClassifications(new HashSet<>(asList(Classification.values())));
		for (final Classification classification : Classification.values()) {
			assertTrue(reference.getClassifications()
				.contains(classification));
		}
	}

	@Test
	public void shouldContainEverythingFromConstructor() {
		reference = new Reference(LEMON, SOME_ID, singleton(BLUE));
		assertEquals(LEMON, reference.getSource());
		assertEquals(SOME_ID, reference.getOriginalId());
		assertEquals(singleton(BLUE), reference.getClassifications());
	}

	@Test
	public void shouldHaveValidToString() {
		reference = new Reference(LEMON, SOME_ID, singleton(BLUE));
		assertToStringIsValid(reference);
	}

	private static final String SOME_ID = "SomeId";
}
