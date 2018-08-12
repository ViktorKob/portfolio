package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DocumentInfosUnitTest {
	private DocumentInfos infos;
	private List<DocumentInfo> actualContainer;

	@Before
	public void setUpForTest() {
		actualContainer = new LinkedList<>();
		infos = new DocumentInfos(actualContainer);
		actualContainer.add(SOME_INFO);
	}

	@Test
	public void shouldBeEmptyInitially() {
		assertFalse(new DocumentInfos().hasData());
	}

	@Test
	public void shouldOverrideOwnContainerOnDemand() {
		final DocumentInfos infos = new DocumentInfos();
		infos.setInfos(actualContainer);
		assertSame(actualContainer, infos.getInfos());
	}

	@Test
	public void shouldContainElementsWhenContainerDoes() {
		infos.setInfos(actualContainer);
		assertTrue(infos.hasData());
	}

	@Test
	public void shouldForwardHashCodeCallToInnerContainer() {
		assertEquals(actualContainer.hashCode(), infos.hashCode());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void shouldForwardEqualsCallToInnerContainer() {
		assertTrue(infos.equals(actualContainer));
	}

	@Test
	public void shouldForwardToStringCallToInnerContainer() {
		assertEquals(actualContainer.toString(), infos.toString());
	}

	private static final DocumentInfo SOME_INFO = new DocumentInfo(new DataTypeId("SomeType", "SomeUid"), new Timestamp(1l), new Timestamp(2l));
}
