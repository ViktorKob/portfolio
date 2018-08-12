package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class EntitiesUnitTest {
	private Entities entities;
	private Collection<DataType> actualContainer;

	@Before
	public void setUpForTest() {
		actualContainer = new LinkedList<>();
		entities = new Entities(actualContainer);
		actualContainer.add(SOME_ENTITY);
	}

	@Test
	public void shouldBeEmptyInitially() {
		assertFalse(new Entities().hasData());
	}

	@Test
	public void shouldOverrideOwnContainerOnDemand() {
		final Entities entities = new Entities();
		entities.setEntities(actualContainer);
		assertSame(actualContainer, entities.getEntities());
	}

	@Test
	public void shouldContainElementsWhenContainerDoes() {
		entities.setEntities(actualContainer);
		assertTrue(entities.hasData());
	}

	@Test
	public void shouldForwardHashCodeCallToInnerContainer() {
		assertEquals(actualContainer.hashCode(), entities.hashCode());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void shouldForwardEqualsCallToInnerContainer() {
		assertTrue(entities.equals(actualContainer));
	}

	@Test
	public void shouldForwardToStringCallToInnerContainer() {
		assertEquals(actualContainer.toString(), entities.toString());
	}

	private static final DataType SOME_ENTITY = new DataType(new DataTypeId("SomeType", "SomeUid"));
}
