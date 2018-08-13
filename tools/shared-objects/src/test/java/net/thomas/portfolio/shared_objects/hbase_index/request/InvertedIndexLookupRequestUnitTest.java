package net.thomas.portfolio.shared_objects.hbase_index.request;

import static java.util.Collections.singleton;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class InvertedIndexLookupRequestUnitTest {
	private InvertedIndexLookupRequest lookup;

	@Before
	public void setup() {
		lookup = new InvertedIndexLookupRequest(ID, LEGAL_INFO, BOUNDS, DOCUMENT_TYPES, RELATIONS);
	}

	@Test
	public void shouldMakeIdenticalCopyUsingConstructor() throws IOException {
		final InvertedIndexLookupRequest copy = new InvertedIndexLookupRequest(lookup);
		assertEquals(lookup, copy);
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(lookup);
	}

	@Test
	public void shouldSurviveNullParameters() {
		assertCanSerializeAndDeserializeWithNullValues(lookup);
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		assertHashCodeIsValidIncludingNullChecks(lookup);
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		assertEqualsIsValidIncludingNullChecks(lookup);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(lookup);
	}

	private static final DataTypeId ID = new DataTypeId("TYPE", "ABCD");
	private static final LegalInformation LEGAL_INFO = new LegalInformation("USER", "JUSTIFICATION", 1l, 2l);
	private static final Bounds BOUNDS = new Bounds(3, 4, 5l, 6l);
	private static final Set<String> DOCUMENT_TYPES = singleton("DOCUMENT_TYPE");
	private static final Set<String> RELATIONS = singleton("RELATION");
}