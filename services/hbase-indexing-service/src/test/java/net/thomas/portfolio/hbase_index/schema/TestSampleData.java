package net.thomas.portfolio.hbase_index.schema;

import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class TestSampleData {
	public static final String SOME_SUBJECT = "subject";
	public static final String SOME_MESSAGE = "message";
	public static final int SOME_DURATION = 1;
	public static final Timestamp SOME_TIMESTAMP = new Timestamp(2l);
	public static final GeoLocation SOME_LOCATION = new GeoLocation(1.1, 2.2);
	public static final Localname SOME_LOCALNAME = new Localname("name");
	public static final DisplayedName SOME_DISPLAYED_NAME = new DisplayedName("name");
	public static final PublicId SOME_PUBLIC_ID = new PublicId("1234");
	public static final PrivateId SOME_PRIVATE_ID = new PrivateId("1234");
	public static final Domain SOME_TOP_LEVEL_DOMAIN = new Domain("part");
	public static final Domain SOME_DOMAIN = new Domain("part", SOME_TOP_LEVEL_DOMAIN);
	public static final EmailAddress SOME_EMAIL_ADDRESS = new EmailAddress(SOME_LOCALNAME, SOME_DOMAIN);
	public static final EmailEndpoint SOME_EMAIL_ENDPOINT = new EmailEndpoint(SOME_DISPLAYED_NAME, SOME_EMAIL_ADDRESS);
	public static final EmailEndpoint EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME = new EmailEndpoint(null, SOME_EMAIL_ADDRESS);
	public static final EmailEndpoint EMAIL_ENDPOINT_MISSING_ADDRESS = new EmailEndpoint(SOME_DISPLAYED_NAME, null);
	public static final CommunicationEndpoint SOME_COMMUNICATION_ENDPOINT = new CommunicationEndpoint(SOME_PUBLIC_ID, SOME_PRIVATE_ID);
	public static final CommunicationEndpoint COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME = new CommunicationEndpoint(null, SOME_PRIVATE_ID);
	public static final CommunicationEndpoint COMMUNICATION_ENDPOINT_MISSING_ADDRESS = new CommunicationEndpoint(SOME_PUBLIC_ID, null);
	public static final Email SOME_EMAIL = new Email(SOME_TIMESTAMP, SOME_TIMESTAMP, SOME_SUBJECT, SOME_MESSAGE, SOME_EMAIL_ENDPOINT,
			asArray(SOME_EMAIL_ENDPOINT), asArray(SOME_EMAIL_ENDPOINT, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME), asArray(SOME_EMAIL_ENDPOINT));
	public static final TextMessage SOME_TEXT_MESSAGE = new TextMessage(SOME_TIMESTAMP, SOME_TIMESTAMP, SOME_MESSAGE, SOME_COMMUNICATION_ENDPOINT,
			SOME_COMMUNICATION_ENDPOINT, SOME_LOCATION, SOME_LOCATION);
	public static final Conversation SOME_CONVERSATION = new Conversation(SOME_TIMESTAMP, SOME_TIMESTAMP, SOME_DURATION, SOME_COMMUNICATION_ENDPOINT,
			SOME_COMMUNICATION_ENDPOINT, SOME_LOCATION, SOME_LOCATION);

	static {
		SOME_DISPLAYED_NAME.uid = "00";
		SOME_LOCALNAME.uid = "01";
		SOME_PUBLIC_ID.uid = "02";
		SOME_PRIVATE_ID.uid = "03";
		SOME_TOP_LEVEL_DOMAIN.uid = "04";
		SOME_DOMAIN.uid = "05";
		SOME_EMAIL_ADDRESS.uid = "06";
		SOME_EMAIL_ENDPOINT.uid = "07";
		EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME.uid = "0700";
		EMAIL_ENDPOINT_MISSING_ADDRESS.uid = "0701";
		SOME_COMMUNICATION_ENDPOINT.uid = "08";
		EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME.uid = "0800";
		EMAIL_ENDPOINT_MISSING_ADDRESS.uid = "0801";
		SOME_EMAIL.uid = "09";
		SOME_TEXT_MESSAGE.uid = "10";
		SOME_CONVERSATION.uid = "11";
	}

	protected static <T> T[] asArray(@SuppressWarnings("unchecked") T... endpoints) {
		return endpoints;
	}
}
