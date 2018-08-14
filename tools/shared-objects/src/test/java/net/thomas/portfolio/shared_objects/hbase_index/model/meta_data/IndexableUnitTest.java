package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static net.thomas.portfolio.testing_tools.EqualsTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.HashCodeTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class IndexableUnitTest {
	private Indexable indexable;

	@Before
	public void setUpForTest() {
		indexable = new Indexable();
	}

	@Test
	public void shouldContainSelectorTypeAfterAddition() {
		indexable.setSelectorType(SOME_SELECTOR_TYPE);
		assertEquals(SOME_SELECTOR_TYPE, indexable.getSelectorType());
	}

	@Test
	public void shouldContainPathAfterAddition() {
		indexable.setPath(SOME_PATH);
		assertEquals(SOME_PATH, indexable.getPath());
	}

	@Test
	public void shouldContainDocumentTypeAfterAddition() {
		indexable.setDocumentType(SOME_DOCUMENT_TYPE);
		assertEquals(SOME_DOCUMENT_TYPE, indexable.getDocumentType());
	}

	@Test
	public void shouldContainDocumentFieldAfterAddition() {
		indexable.setDocumentField(SOME_DOCUMENT_FIELD);
		assertEquals(SOME_DOCUMENT_FIELD, indexable.getDocumentField());
	}

	@Test
	public void shouldContainEverythingFromConstructor() {
		indexable = new Indexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_DOCUMENT_FIELD);
		assertEquals(SOME_SELECTOR_TYPE, indexable.getSelectorType());
		assertEquals(SOME_PATH, indexable.getPath());
		assertEquals(SOME_DOCUMENT_TYPE, indexable.getDocumentType());
		assertEquals(SOME_DOCUMENT_FIELD, indexable.getDocumentField());
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		indexable = new Indexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_DOCUMENT_FIELD);
		assertCanSerializeAndDeserialize(indexable);
	}

	@Test
	public void shouldSurviveNullParameters() {
		indexable = new Indexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_DOCUMENT_FIELD);
		assertCanSerializeAndDeserializeWithNullValues(indexable);
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		indexable = new Indexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_DOCUMENT_FIELD);
		assertHashCodeIsValidIncludingNullChecks(indexable);
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		indexable = new Indexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_DOCUMENT_FIELD);
		assertEqualsIsValidIncludingNullChecks(indexable);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		indexable = new Indexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_DOCUMENT_FIELD);
		assertToStringContainsAllFieldsFromObject(indexable);
	}

	private static final String SOME_SELECTOR_TYPE = "SomeSelectorType";
	private static final String SOME_PATH = "SomePath";
	private static final String SOME_DOCUMENT_TYPE = "SomeDocumentType";
	private static final String SOME_DOCUMENT_FIELD = "SomeDocumentField";
}
