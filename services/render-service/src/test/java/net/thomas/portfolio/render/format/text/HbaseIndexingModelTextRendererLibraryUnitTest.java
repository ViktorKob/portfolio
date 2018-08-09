package net.thomas.portfolio.render.format.text;

import static java.lang.String.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.render.common.context.TextRenderContextBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter.Iec8601DateConverter;

public class HbaseIndexingModelTextRendererLibraryUnitTest {
	private static final String SOME_UID = "AABBCCDD";
	private static final String SOME_STRING = "Some string";
	private static final String ANOTHER_STRING = "anotherString";
	private static final Timestamp SOME_TIMESTAMP = new Timestamp(1000l);
	private static final int SOME_DURATION = 2;

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
	public void shouldRenderSimpleFieldCorrectly() {
		final Selector selector = setupLocalname(SOME_STRING);
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
		final Selector selector = setupDomainWithParent(ANOTHER_STRING, parentDomain);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderEmailAddressCorrectly() {
		final Selector selector = setupEmailAddress(setupLocalname(SOME_STRING), setupDomain(SOME_STRING));
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING + "@" + SOME_STRING));
	}

	@Test
	public void shouldRenderEmailAddressInEndpointClearlyAndCorrectly() {
		final Selector displayedName = setupDisplayedName(ANOTHER_STRING);
		final Selector emailAddress = setupEmailAddress(setupLocalname(SOME_STRING), setupDomain(SOME_STRING));
		final RawDataType selector = setupEmailEndpoint(displayedName, emailAddress);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains("<( " + SOME_STRING + "@" + SOME_STRING + " )>"));
	}

	@Test
	public void shouldRenderDisplayedNameInEndpointCorrectly() {
		final Selector displayedName = setupDisplayedName(SOME_STRING);
		final Selector emailAddress = setupEmailAddress(setupLocalname(ANOTHER_STRING), setupDomain(ANOTHER_STRING));
		final RawDataType selector = setupEmailEndpoint(displayedName, emailAddress);
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderEmailAddressIfDisplayedNameIsMissing() {
		final RawDataType selector = setupEmailEndpoint(null, setupEmailAddress(setupLocalname(SOME_STRING), setupDomain(SOME_STRING)));
		final String renderedEntity = library.render(selector, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING + "@" + SOME_STRING));
	}

	@Test
	public void shouldRenderPublicIdInEndpointClearlyAndCorrectly() {
		final RawDataType endpoint = setupCommunicationEndpoint(setupPublicId(SOME_STRING), null);
		final String renderedEntity = library.render(endpoint, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains("PublicId: " + SOME_STRING));
	}

	@Test
	public void shouldRenderPrivateIdInEndpointClearlyAndCorrectly() {
		final RawDataType endpoint = setupCommunicationEndpoint(null, setupPrivateId(SOME_STRING));
		final String renderedEntity = library.render(endpoint, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains("PrivateId: " + SOME_STRING));
	}

	@Test
	public void shouldRenderBothIdInEndpointWhenBothArePresent() {
		final RawDataType endpoint = setupCommunicationEndpoint(setupPublicId(SOME_STRING), setupPrivateId(SOME_STRING));
		final String renderedEntity = library.render(endpoint, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains("PublicId: " + SOME_STRING));
		assertTrue(renderedEntity, renderedEntity.contains("PrivateId: " + SOME_STRING));
	}

	@Test
	public void shouldRenderFromEndpointInEmail() {
		final Selector emailAddress = setupEmailAddress(setupLocalname(SOME_STRING), setupDomain(SOME_STRING));
		final RawDataType fromEndpoint = setupEmailEndpoint(null, emailAddress);
		final Document document = setupEmail(ANOTHER_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING + "@" + SOME_STRING));
	}

	@Test
	public void shouldRenderSubjectInEmail() {
		final Selector emailAddress = setupEmailAddress(setupLocalname(ANOTHER_STRING), setupDomain(ANOTHER_STRING));
		final RawDataType fromEndpoint = setupEmailEndpoint(null, emailAddress);
		final Document document = setupEmail(SOME_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderTimeOfEventInEmail() {
		final Selector emailAddress = setupEmailAddress(setupLocalname(ANOTHER_STRING), setupDomain(ANOTHER_STRING));
		final RawDataType fromEndpoint = setupEmailEndpoint(null, emailAddress);
		final Document document = setupEmail(ANOTHER_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(converter.formatTimestamp(SOME_TIMESTAMP.getTimestamp())));
	}

	@Test
	public void shouldShortenEmailRenderingTo250Characters() {
		final Selector emailAddress = setupEmailAddress(setupLocalname(ANOTHER_STRING), setupDomain(ANOTHER_STRING));
		final RawDataType fromEndpoint = setupEmailEndpoint(null, emailAddress);
		final Document document = setupEmail(repeatString(SOME_STRING, 250), SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertEquals(250, renderedEntity.length());
	}

	@Test
	public void shouldRenderSenderEndpointInTextMessage() {
		final RawDataType fromEndpoint = setupCommunicationEndpoint(setupPublicId(SOME_STRING), null);
		final Document document = setupTextMessage(ANOTHER_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderMessageInTextMessage() {
		final RawDataType fromEndpoint = setupCommunicationEndpoint(setupPublicId(ANOTHER_STRING), null);
		final Document document = setupTextMessage(SOME_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderTimeOfEventInTextMessage() {
		final RawDataType fromEndpoint = setupCommunicationEndpoint(setupPublicId(ANOTHER_STRING), null);
		final Document document = setupTextMessage(ANOTHER_STRING, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(converter.formatTimestamp(SOME_TIMESTAMP.getTimestamp())));
	}

	@Test
	public void shouldShortenTextMessageRenderingTo250Characters() {
		final RawDataType fromEndpoint = setupCommunicationEndpoint(setupPublicId(ANOTHER_STRING), null);
		final Document document = setupTextMessage(repeatString(SOME_STRING, 250), SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertEquals(250, renderedEntity.length());
	}

	@Test
	public void shouldRenderPrimaryEndpointInConversation() {
		final RawDataType fromEndpoint = setupCommunicationEndpoint(setupPublicId(SOME_STRING), null);
		final Document document = setupConversation(SOME_DURATION, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(SOME_STRING));
	}

	@Test
	public void shouldRenderDurationInSecondsInConversation() {
		final RawDataType fromEndpoint = setupCommunicationEndpoint(setupPublicId(SOME_STRING), null);
		final Document document = setupConversation(SOME_DURATION, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(valueOf(SOME_DURATION)));
	}

	@Test
	public void shouldRenderTimeOfEventInConversation() {
		final RawDataType fromEndpoint = setupCommunicationEndpoint(setupPublicId(SOME_STRING), null);
		final Document document = setupConversation(SOME_DURATION, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertTrue(renderedEntity, renderedEntity.contains(converter.formatTimestamp(SOME_TIMESTAMP.getTimestamp())));
	}

	@Test
	public void shouldShortenConversationRenderingTo250Characters() {
		final RawDataType fromEndpoint = setupCommunicationEndpoint(setupPublicId(repeatString(SOME_STRING, 250)), null);
		final Document document = setupConversation(SOME_DURATION, SOME_TIMESTAMP, fromEndpoint);
		final String renderedEntity = library.render(document, contextBuilder.build());
		assertEquals(250, renderedEntity.length());
	}

	private Selector setupLocalname(String name) {
		return setupSimpleType(mock(Selector.class), "Localname", "name", name);
	}

	private Selector setupDisplayedName(String name) {
		return setupSimpleType(mock(Selector.class), "DisplayedName", "name", name);
	}

	private Selector setupPublicId(String number) {
		return setupSimpleType(mock(Selector.class), "PublicId", "number", number);
	}

	private Selector setupPrivateId(String number) {
		return setupSimpleType(mock(Selector.class), "PrivateId", "number", number);
	}

	private Selector setupDomain(String domainPart) {
		return setupSimpleType(mock(Selector.class), "Domain", "domainPart", domainPart);
	}

	private Selector setupDomainWithParent(String domainPart, Selector parentDomain) {
		final Selector domain = setupSimpleType(mock(Selector.class), "Domain", "domainPart", domainPart);
		when(domain.get(eq("domain"))).thenReturn(parentDomain);
		return domain;
	}

	private Selector setupEmailAddress(final Selector localname, final Selector domain) {
		final Selector emailAddress = mock(Selector.class);
		when(emailAddress.getId()).thenReturn(new DataTypeId("EmailAddress", SOME_UID));
		when(emailAddress.get(eq("localname"))).thenReturn(localname);
		when(emailAddress.get(eq("domain"))).thenReturn(domain);
		return emailAddress;
	}

	private RawDataType setupEmailEndpoint(Selector displayedName, Selector emailAddress) {
		final RawDataType emailEndpoint = mock(RawDataType.class);
		when(emailEndpoint.getId()).thenReturn(new DataTypeId("EmailEndpoint", SOME_UID));
		when(emailEndpoint.get(eq("displayedName"))).thenReturn(displayedName);
		when(emailEndpoint.get(eq("address"))).thenReturn(emailAddress);
		when(emailEndpoint.containsKey("displayedName")).thenReturn(displayedName != null);
		return emailEndpoint;
	}

	private RawDataType setupCommunicationEndpoint(Selector publicId, Selector privateId) {
		final RawDataType communicationEndpoint = mock(RawDataType.class);
		when(communicationEndpoint.getId()).thenReturn(new DataTypeId("CommunicationEndpoint", SOME_UID));
		when(communicationEndpoint.get(eq("publicId"))).thenReturn(publicId);
		when(communicationEndpoint.get(eq("privateId"))).thenReturn(privateId);
		when(communicationEndpoint.containsKey("publicId")).thenReturn(publicId != null);
		when(communicationEndpoint.containsKey("privateId")).thenReturn(privateId != null);
		return communicationEndpoint;
	}

	private Document setupEmail(String subject, Timestamp timeOfEvent, RawDataType fromEndpoint) {
		final Document email = mock(Document.class);
		when(email.getId()).thenReturn(new DataTypeId("Email", SOME_UID));
		when(email.get(eq("subject"))).thenReturn(subject);
		when(email.getTimeOfEvent()).thenReturn(timeOfEvent);
		when(email.get(eq("from"))).thenReturn(fromEndpoint);
		return email;
	}

	private Document setupTextMessage(String message, Timestamp timeOfEvent, RawDataType fromEndpoint) {
		final Document textMessage = mock(Document.class);
		when(textMessage.getId()).thenReturn(new DataTypeId("TextMessage", SOME_UID));
		when(textMessage.get(eq("message"))).thenReturn(message);
		when(textMessage.getTimeOfEvent()).thenReturn(timeOfEvent);
		when(textMessage.get(eq("sender"))).thenReturn(fromEndpoint);
		return textMessage;
	}

	private Document setupConversation(int durationInSeconds, Timestamp timeOfEvent, RawDataType fromEndpoint) {
		final Document conversation = mock(Document.class);
		when(conversation.getId()).thenReturn(new DataTypeId("Conversation", SOME_UID));
		when(conversation.get(eq("durationInSeconds"))).thenReturn(durationInSeconds);
		when(conversation.getTimeOfEvent()).thenReturn(timeOfEvent);
		when(conversation.get(eq("primary"))).thenReturn(fromEndpoint);
		return conversation;
	}

	private Selector setupSimpleType(Selector selector, String type, String field, String value) {
		when(selector.getId()).thenReturn(new DataTypeId(type, SOME_UID));
		when(selector.get(eq(field))).thenReturn(value);
		return selector;
	}

	private String repeatString(String string, int times) {
		final StringBuilder builder = new StringBuilder();
		while (times-- > 0) {
			builder.append(string);
		}
		return builder.toString();
	}
}
