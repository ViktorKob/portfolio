package net.thomas.portfolio.shared_objects.hbase_index.model.util;

import static net.thomas.portfolio.shared_objects.hbase_index.model.data.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField.dataType;
import static org.junit.Assert.assertEquals;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class IdGeneratorUnitTest {
	private static final boolean KEYS_SHOULD_BE_CONSISTENT = false;

	private static final Fields LOCALNAME_FIELDS;
	private static final Fields DOMAIN_FIELDS;
	private static final Fields EMAIL_ADDRESS_FIELDS;
	private static final Selector LOCALNAME;
	private static final Selector DOMAIN;
	private static final Selector EMAIL_ADDRESS;
	private static final UidConverter KEY_CONVERTER;

	private IdGenerator localnameGenerator;
	private IdGenerator domainGenerator;
	private IdGenerator emailAddressGenerator;

	@Before
	public void setupDataTypes() {
		localnameGenerator = new IdGenerator(LOCALNAME_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		domainGenerator = new IdGenerator(DOMAIN_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		emailAddressGenerator = new IdGenerator(EMAIL_ADDRESS_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
	}

	@Test
	public void shouldIncludeFieldInCalculation() throws NoSuchAlgorithmException {
		final DataTypeId actualId = localnameGenerator.calculateId("Localname", LOCALNAME);
		final String expectedUid = LOCALNAME.getId().uid;
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldHandleNullReferenceCorrectly() throws NoSuchAlgorithmException {
		final DataTypeId actualId = domainGenerator.calculateId("Domain", DOMAIN);
		final String expectedUid = DOMAIN.getId().uid;
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldIncludeSubTypeIdsInCalculation() throws NoSuchAlgorithmException {
		final DataTypeId actualId = emailAddressGenerator.calculateId("EmailAddress", EMAIL_ADDRESS);
		final String expectedUid = EMAIL_ADDRESS.getId().uid;
		assertEquals(expectedUid, actualId.uid);
	}

	static {
		LOCALNAME_FIELDS = fields(string("name"));
		DOMAIN_FIELDS = fields(string("domainPart"), dataType("domain", "Domain"));
		EMAIL_ADDRESS_FIELDS = fields(dataType("localname", "Localname"), dataType("domain", "Domain"));

		LOCALNAME = new Selector();
		LOCALNAME.put("name", "LOCALNAME_NAME");
		DOMAIN = new Selector();
		DOMAIN.put("domainPart", "DOMAIN_PART");
		EMAIL_ADDRESS = new Selector();
		EMAIL_ADDRESS.put("localname", LOCALNAME);
		EMAIL_ADDRESS.put("domain", DOMAIN);
		KEY_CONVERTER = new UidConverter();
		try {
			LOCALNAME.setId(new DataTypeId("Localname", hashLocalname()));
			DOMAIN.setId(new DataTypeId("Domain", hashDomain()));
			EMAIL_ADDRESS.setId(new DataTypeId("EmailAddress", hashEmailAddress()));
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private static String hashLocalname() throws NoSuchAlgorithmException {
		final MessageDigest hasher = hasher();
		hasher.update("Localname".getBytes());
		hasher.update(getField(LOCALNAME, "name").getBytes());
		return KEY_CONVERTER.convert(hasher.digest());
	}

	private static String hashDomain() throws NoSuchAlgorithmException {
		final MessageDigest hasher = hasher();
		hasher.update("Domain".getBytes());
		hasher.update(getField(DOMAIN, "domainPart").getBytes());
		return KEY_CONVERTER.convert(hasher.digest());
	}

	private static String hashEmailAddress() throws NoSuchAlgorithmException {
		final MessageDigest hasher = hasher();
		hasher.update("EmailAddress".getBytes());
		hasher.update(hashLocalname().getBytes());
		hasher.update(hashDomain().getBytes());
		return KEY_CONVERTER.convert(hasher.digest());
	}

	private static MessageDigest hasher() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("MD5");
	}

	private static String getField(Selector selector, String field) {
		return selector.get(field);
	}
}
