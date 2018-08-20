package net.thomas.portfolio.shared_objects.hbase_index.request;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static net.thomas.portfolio.testing_tools.EqualsTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.HashCodeTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.common.services.parameters.ParameterGroup;
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
		assertEquals(lookup, new InvertedIndexLookupRequest(lookup));
	}

	@Test
	public void shouldAddLegalInfoToGroups() throws IOException {
		final Set<ParameterGroup> groups = new HashSet<>(asList(lookup.getGroups()));
		assertTrue(groups.contains(LEGAL_INFO));
	}

	@Test
	public void shouldAddBoundsToGroups() throws IOException {
		final Set<ParameterGroup> groups = new HashSet<>(asList(lookup.getGroups()));
		assertTrue(groups.contains(BOUNDS));
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