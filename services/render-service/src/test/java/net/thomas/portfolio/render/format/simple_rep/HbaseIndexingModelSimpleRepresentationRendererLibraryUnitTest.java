package net.thomas.portfolio.render.format.simple_rep;

import static net.thomas.portfolio.render.format.DataTypeMockingUtil.ANOTHER_STRING;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.SOME_STRING;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.SOME_UID;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockDisplayedName;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockDomainWithParent;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockEmailAddress;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockLocalname;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockPrivateId;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockPublicId;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.setupDomain;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContextBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class HbaseIndexingModelSimpleRepresentationRendererLibraryUnitTest {
	private HbaseIndexingModelSimpleRepresentationRendererLibrary library;
	private SimpleRepresentationRenderContextBuilder contextBuilder;

	@Before
	public void setUpForTest() {
		library = new HbaseIndexingModelSimpleRepresentationRendererLibrary();
		contextBuilder = new SimpleRepresentationRenderContextBuilder();
	}

	@Test
	public void shouldRenderLocalnameCorrectly() {
		final Selector selector = mockLocalname(SOME_STRING);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderDisplayedNameCorrectly() {
		final Selector selector = mockDisplayedName(SOME_STRING);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderPublicIdCorrectly() {
		final Selector selector = mockPublicId(SOME_STRING);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderPrivateIdCorrectly() {
		final Selector selector = mockPrivateId(SOME_STRING);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderErrorMessageForUnknownType() {
		final Selector selector = mock(Selector.class);
		when(selector.getId()).thenReturn(new DataTypeId("UnknownType", SOME_UID));
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains("Unable to render"));
	}

	@Test
	public void shouldRenderDomainPartCorrectly() {
		final Selector selector = setupDomain(SOME_STRING);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderParentDomainPartCorrectly() {
		final Selector parentDomain = setupDomain(SOME_STRING);
		final Selector selector = mockDomainWithParent(ANOTHER_STRING, parentDomain);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderEmailAddressCorrectly() {
		final Selector selector = mockEmailAddress(mockLocalname(SOME_STRING), setupDomain(SOME_STRING));
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING + "@" + SOME_STRING));
	}

	@Test
	public void shouldHaveFunctioningToStringMethod() {
		assertToStringContainsAllFieldsFromObject(library);
	}
}