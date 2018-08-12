package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static java.util.Collections.emptySet;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Source.APPLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class ReferencesUnitTest {
	private References references;
	private Collection<Reference> actualContainer;

	@Before
	public void setUpForTest() {
		actualContainer = new LinkedList<>();
		references = new References(actualContainer);
		actualContainer.add(SOME_REFERENCE);
	}

	@Test
	public void shouldBeEmptyInitially() {
		assertFalse(new References().hasData());
	}

	@Test
	public void shouldOverrideOwnContainerOnDemand() {
		final References references = new References();
		references.setReferences(actualContainer);
		assertSame(actualContainer, references.getReferences());
	}

	@Test
	public void shouldContainElementsWhenContainerDoes() {
		references.setReferences(actualContainer);
		assertTrue(references.hasData());
	}

	@Test
	public void shouldForwardHashCodeCallToInnerContainer() {
		assertEquals(actualContainer.hashCode(), references.hashCode());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void shouldForwardEqualsCallToInnerContainer() {
		assertTrue(references.equals(actualContainer));
	}

	@Test
	public void shouldForwardToStringCallToInnerContainer() {
		assertEquals(actualContainer.toString(), references.toString());
	}

	private static final Reference SOME_REFERENCE = new Reference(APPLE, "originalId", emptySet());
}
