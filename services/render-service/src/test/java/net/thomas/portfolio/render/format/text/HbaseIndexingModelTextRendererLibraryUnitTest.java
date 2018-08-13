package net.thomas.portfolio.render.format.text;

import static java.lang.String.valueOf;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.ANOTHER_STRING;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.SOME_DURATION;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.SOME_STRING;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.SOME_TIMESTAMP;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.SOME_UID;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockCommunicationEndpoint;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockConversation;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockDisplayedName;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockDomainWithParent;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockEmail;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockEmailAddress;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockEmailEndpoint;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockLocalname;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockPrivateId;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockPublicId;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.mockTextMessage;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.repeatString;
import static net.thomas.portfolio.render.format.DataTypeMockingUtil.setupDomain;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.render.common.context.TextRenderContextBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter.Iec8601DateConverter;

public class HbaseIndexingModelTextRendererLibraryUnitTest {
	private HbaseIndexingModelTextRendererLibrary library;
	private TextRenderContextBuilder contextBuilder;
	private Iec8601DateConverter converter;

	@Before
	public void setUpForTest() {
		library = new HbaseIndexingModelTextRendererLibrary();
		contextBuilder = new TextRenderContextBuilder();
		converter = new DateConverter.Iec8601DateConverter();
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
	public void shouldRenderEmailAddressInEndpointClearlyAndCorrectly() {
		final Selector displayedName = mockDisplayedName(ANOTHER_STRING);
		final Selector emailAddress = mockEmailAddress(mockLocalname(SOME_STRING), setupDomain(SOME_STRING));
		final RawDataType selector = mockEmailEndpoint(displayedName, emailAddress);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains("<( " + SOME_STRING + "@" + SOME_STRING + " )>"));
	}

	@Test
	public void shouldRenderDisplayedNameInEndpointCorrectly() {
		final Selector displayedName = mockDisplayedName(SOME_STRING);
		final Selector emailAddress = mockEmailAddress(mockLocalname(ANOTHER_STRING), setupDomain(ANOTHER_STRING));
		final RawDataType selector = mockEmailEndpoint(displayedName, emailAddress);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderEmailAddressIfDisplayedNameIsMissing() {
		final RawDataType selector = mockEmailEndpoint(null, mockEmailAddress(mockLocalname(SOME_STRING), setupDomain(SOME_STRING)));
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING + "@" + SOME_STRING));
	}

	@Test
	public void shouldRenderPublicIdInEndpointClearlyAndCorrectly() {
		final RawDataType endpoint = mockCommunicationEndpoint(mockPublicId(SOME_STRING), null);
		final String renderedEntity = library.render(endpoint, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains("PublicId: " + SOME_STRING));
	}

	@Test
	public void shouldRenderPrivateIdInEndpointClearlyAndCorrectly() {
		final RawDataType endpoint = mockCommunicationEndpoint(null, mockPrivateId(SOME_STRING));
		final String renderedEntity = library.render(endpoint, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains("PrivateId: " + SOME_STRING));
	}

	@Test
	public void shouldRenderBothIdInEndpointWhenBothArePresent() {
		final RawDataType endpoint = mockCommunicationEndpoint(mockPublicId(SOME_STRING), mockPrivateId(SOME_STRING));
		final String renderedEntity = library.render(endpoint, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains("PublicId: " + SOME_STRING));
		assertTrue(renderedEntity, renderedEntity.contains("PrivateId: " + SOME_STRING));
	}

	@Test
	public void shouldRenderFromEndpointInEmail() {
		final Selector emailAddress = mockEmailAddress(mockLocalname(SOME_STRING), setupDomain(SOME_STRING));
		final RawDataType fromEndpoint = mockEmailEndpoint(null, emailAddress);
		final Document document = mockEmail(ANOTHER_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING + "@" + SOME_STRING));
	}

	@Test
	public void shouldRenderSubjectInEmail() {
		final Selector emailAddress = mockEmailAddress(mockLocalname(ANOTHER_STRING), setupDomain(ANOTHER_STRING));
		final RawDataType fromEndpoint = mockEmailEndpoint(null, emailAddress);
		final Document document = mockEmail(SOME_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderTimeOfEventInEmail() {
		final Selector emailAddress = mockEmailAddress(mockLocalname(ANOTHER_STRING), setupDomain(ANOTHER_STRING));
		final RawDataType fromEndpoint = mockEmailEndpoint(null, emailAddress);
		final Document document = mockEmail(ANOTHER_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(converter.formatTimestamp(SOME_TIMESTAMP.getTimestamp())));
	}

	@Test
	public void shouldShortenEmailRenderingTo250Characters() {
		final Selector emailAddress = mockEmailAddress(mockLocalname(ANOTHER_STRING), setupDomain(ANOTHER_STRING));
		final RawDataType fromEndpoint = mockEmailEndpoint(null, emailAddress);
		final Document document = mockEmail(repeatString(SOME_STRING, 250), SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertEquals(250, renderedEntity.length());
	}

	@Test
	public void shouldRenderSenderEndpointInTextMessage() {
		final RawDataType fromEndpoint = mockCommunicationEndpoint(mockPublicId(SOME_STRING), null);
		final Document document = mockTextMessage(ANOTHER_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderMessageInTextMessage() {
		final RawDataType fromEndpoint = mockCommunicationEndpoint(mockPublicId(ANOTHER_STRING), null);
		final Document document = mockTextMessage(SOME_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderTimeOfEventInTextMessage() {
		final RawDataType fromEndpoint = mockCommunicationEndpoint(mockPublicId(ANOTHER_STRING), null);
		final Document document = mockTextMessage(ANOTHER_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(converter.formatTimestamp(SOME_TIMESTAMP.getTimestamp())));
	}

	@Test
	public void shouldShortenTextMessageRenderingTo250Characters() {
		final RawDataType fromEndpoint = mockCommunicationEndpoint(mockPublicId(ANOTHER_STRING), null);
		final Document document = mockTextMessage(repeatString(SOME_STRING, 250), SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertEquals(250, renderedEntity.length());
	}

	@Test
	public void shouldRenderPrimaryEndpointInConversation() {
		final RawDataType fromEndpoint = mockCommunicationEndpoint(mockPublicId(SOME_STRING), null);
		final Document document = mockConversation(SOME_DURATION, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderDurationInSecondsInConversation() {
		final RawDataType fromEndpoint = mockCommunicationEndpoint(mockPublicId(SOME_STRING), null);
		final Document document = mockConversation(SOME_DURATION, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(valueOf(SOME_DURATION)));
	}

	@Test
	public void shouldRenderTimeOfEventInConversation() {
		final RawDataType fromEndpoint = mockCommunicationEndpoint(mockPublicId(SOME_STRING), null);
		final Document document = mockConversation(SOME_DURATION, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(converter.formatTimestamp(SOME_TIMESTAMP.getTimestamp())));
	}

	@Test
	public void shouldShortenConversationRenderingTo250Characters() {
		final RawDataType fromEndpoint = mockCommunicationEndpoint(mockPublicId(repeatString(SOME_STRING, 250)), null);
		final Document document = mockConversation(SOME_DURATION, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertEquals(250, renderedEntity.length());
	}

	@Test
	public void shouldHaveFunctioningToStringMethod() {
		assertToStringContainsAllFieldsFromObject(library);
	}
}
