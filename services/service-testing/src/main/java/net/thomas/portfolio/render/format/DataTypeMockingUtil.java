package net.thomas.portfolio.render.format;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class DataTypeMockingUtil {
	public static final String SOME_UID = "AABBCCDD";
	public static final String SOME_STRING = "Some string";
	public static final String ANOTHER_STRING = "anotherString";
	public static final Timestamp SOME_TIMESTAMP = new Timestamp(1000l);
	public static final int SOME_DURATION = 2;

	public static Selector mockLocalname(String name) {
		return mockSimpleType(mock(Selector.class), "Localname", "name", name);
	}

	public static Selector mockDisplayedName(String name) {
		return mockSimpleType(mock(Selector.class), "DisplayedName", "name", name);
	}

	public static Selector mockPublicId(String number) {
		return mockSimpleType(mock(Selector.class), "PublicId", "number", number);
	}

	public static Selector mockPrivateId(String number) {
		return mockSimpleType(mock(Selector.class), "PrivateId", "number", number);
	}

	public static Selector setupDomain(String domainPart) {
		return mockSimpleType(mock(Selector.class), "Domain", "domainPart", domainPart);
	}

	public static Selector mockDomainWithParent(String domainPart, Selector parentDomain) {
		final Selector domain = mockSimpleType(mock(Selector.class), "Domain", "domainPart", domainPart);
		when(domain.get(eq("domain"))).thenReturn(parentDomain);
		return domain;
	}

	public static Selector mockEmailAddress(final Selector localname, final Selector domain) {
		final Selector emailAddress = mock(Selector.class);
		when(emailAddress.getId()).thenReturn(new DataTypeId("EmailAddress", SOME_UID));
		when(emailAddress.get(eq("localname"))).thenReturn(localname);
		when(emailAddress.get(eq("domain"))).thenReturn(domain);
		return emailAddress;
	}

	public static RawDataType mockEmailEndpoint(Selector displayedName, Selector emailAddress) {
		final RawDataType emailEndpoint = mock(RawDataType.class);
		when(emailEndpoint.getId()).thenReturn(new DataTypeId("EmailEndpoint", SOME_UID));
		when(emailEndpoint.get(eq("displayedName"))).thenReturn(displayedName);
		when(emailEndpoint.get(eq("address"))).thenReturn(emailAddress);
		when(emailEndpoint.containsKey("displayedName")).thenReturn(displayedName != null);
		return emailEndpoint;
	}

	public static RawDataType mockCommunicationEndpoint(Selector publicId, Selector privateId) {
		final RawDataType communicationEndpoint = mock(RawDataType.class);
		when(communicationEndpoint.getId()).thenReturn(new DataTypeId("CommunicationEndpoint", SOME_UID));
		when(communicationEndpoint.get(eq("publicId"))).thenReturn(publicId);
		when(communicationEndpoint.get(eq("privateId"))).thenReturn(privateId);
		when(communicationEndpoint.containsKey("publicId")).thenReturn(publicId != null);
		when(communicationEndpoint.containsKey("privateId")).thenReturn(privateId != null);
		return communicationEndpoint;
	}

	public static Document mockEmail(String subject, Timestamp timeOfEvent, RawDataType fromEndpoint) {
		final Document email = mock(Document.class);
		when(email.getId()).thenReturn(new DataTypeId("Email", SOME_UID));
		when(email.get(eq("subject"))).thenReturn(subject);
		when(email.getTimeOfEvent()).thenReturn(timeOfEvent);
		when(email.get(eq("from"))).thenReturn(fromEndpoint);
		return email;
	}

	public static Document mockTextMessage(String message, Timestamp timeOfEvent, RawDataType fromEndpoint) {
		final Document textMessage = mock(Document.class);
		when(textMessage.getId()).thenReturn(new DataTypeId("TextMessage", SOME_UID));
		when(textMessage.get(eq("message"))).thenReturn(message);
		when(textMessage.getTimeOfEvent()).thenReturn(timeOfEvent);
		when(textMessage.get(eq("sender"))).thenReturn(fromEndpoint);
		return textMessage;
	}

	public static Document mockConversation(int durationInSeconds, Timestamp timeOfEvent, RawDataType fromEndpoint) {
		final Document conversation = mock(Document.class);
		when(conversation.getId()).thenReturn(new DataTypeId("Conversation", SOME_UID));
		when(conversation.get(eq("durationInSeconds"))).thenReturn(durationInSeconds);
		when(conversation.getTimeOfEvent()).thenReturn(timeOfEvent);
		when(conversation.get(eq("primary"))).thenReturn(fromEndpoint);
		return conversation;
	}

	public static Selector mockSimpleType(Selector selector, String type, String field, String value) {
		when(selector.getId()).thenReturn(new DataTypeId(type, SOME_UID));
		when(selector.get(eq(field))).thenReturn(value);
		return selector;
	}

	public static String repeatString(String string, int times) {
		final StringBuilder builder = new StringBuilder();
		while (times-- > 0) {
			builder.append(string);
		}
		return builder.toString();
	}
}