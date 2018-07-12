package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField.dataType;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class SimpleRepresentationParserLibraryUnitTest {
	private static final String SIMPLE_TYPE = "Localname";
	private static final String SIMPLE_TYPE_SIMPLE_REP = "ABCD";
	private static final String DOMAIN_TYPE = "Domain";
	private static final String DOMAIN_SIMPLE_REP = "ABCD.ABCD.AB";
	private static final String EMAIL_ADDRESS_TYPE = "EmailAddress";
	private static final String EMAIL_ADDRESS_SIMPLE_REP = "ABCD@ABCD.ABCD.AB";

	private SimpleRepresentationParserLibrary library;

	@Before
	public void setupForTest() {
		final HbaseIndexSchema modelMock = mock(HbaseIndexSchema.class);
		when(modelMock.getFieldsForDataType(same(SIMPLE_TYPE))).thenReturn(LOCALNAME_FIELDS);
		when(modelMock.getFieldsForDataType(same(DOMAIN_TYPE))).thenReturn(DOMAIN_FIELDS);
		when(modelMock.getFieldsForDataType(same(EMAIL_ADDRESS_TYPE))).thenReturn(EMAIL_ADDRESS_FIELDS);
		library = new SimpleRepresentationParserLibrary(modelMock);
	}

	@Test
	public void shouldCalculateCorrectUidForLocalname() throws NoSuchAlgorithmException {
		final Selector selector = library.parse(SIMPLE_TYPE, SIMPLE_TYPE_SIMPLE_REP);
		assertEquals(calculateSimpleUid(SIMPLE_TYPE, SIMPLE_TYPE_SIMPLE_REP), selector.getId().uid);
	}

	@Test
	public void shouldCalculateCorrectUidForDomain() throws NoSuchAlgorithmException {
		final Selector selector = library.parse(DOMAIN_TYPE, DOMAIN_SIMPLE_REP);
		assertEquals(calculateDomainUid(DOMAIN_SIMPLE_REP), selector.getId().uid);
	}

	@Test
	public void shouldCalculateCorrectUidForEmailAddress() throws NoSuchAlgorithmException {
		final Selector selector = library.parse(EMAIL_ADDRESS_TYPE, EMAIL_ADDRESS_SIMPLE_REP);
		assertEquals(calculateEmailAddressUid(calculateSimpleUid(SIMPLE_TYPE, SIMPLE_TYPE_SIMPLE_REP), calculateDomainUid(DOMAIN_SIMPLE_REP)),
				selector.getId().uid);
	}

	private String calculateSimpleUid(String type, String simpleRep) throws NoSuchAlgorithmException {
		final MessageDigest hasher = MessageDigest.getInstance("MD5");
		hasher.update(type.getBytes());
		hasher.update(simpleRep.getBytes());
		return printHexBinary(hasher.digest());
	}

	private String calculateDomainUid(String domainSimpleRep) throws NoSuchAlgorithmException {
		final MessageDigest hasher = MessageDigest.getInstance("MD5");
		final Stack<String> domainParts = new Stack<>();
		for (final String domainPart : domainSimpleRep.split("\\.")) {
			domainParts.add(domainPart);
		}
		String parentUid = null;
		while (!domainParts.isEmpty()) {
			hasher.reset();
			hasher.update(DOMAIN_TYPE.getBytes());
			hasher.update(domainParts.pop()
				.getBytes());
			if (parentUid != null) {
				hasher.update(parentUid.getBytes());
			}
			parentUid = printHexBinary(hasher.digest());
		}
		return parentUid;
	}

	private String calculateEmailAddressUid(String localnameUid, String domainUid) throws NoSuchAlgorithmException {
		final MessageDigest hasher = MessageDigest.getInstance("MD5");
		hasher.update(EMAIL_ADDRESS_TYPE.getBytes());
		hasher.update(localnameUid.getBytes());
		hasher.update(domainUid.getBytes());
		return printHexBinary(hasher.digest());
	}

	private static final Fields LOCALNAME_FIELDS;
	private static final Fields DOMAIN_FIELDS;
	private static final Fields EMAIL_ADDRESS_FIELDS;

	static {
		LOCALNAME_FIELDS = fields(string("name"));
		DOMAIN_FIELDS = fields(string("domainPart"), dataType("domain", "Domain"));
		EMAIL_ADDRESS_FIELDS = fields(dataType("localname", "Localname"), dataType("domain", "Domain"));
	}
}