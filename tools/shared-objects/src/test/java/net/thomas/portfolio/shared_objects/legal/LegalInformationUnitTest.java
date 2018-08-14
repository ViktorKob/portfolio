package net.thomas.portfolio.shared_objects.legal;

import static net.thomas.portfolio.shared_objects.test_utils.ParameterGroupTestUtil.assertParametersMatchParameterGroups;
import static net.thomas.portfolio.testing_tools.EqualsTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.HashCodeTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class LegalInformationUnitTest {
	private LegalInformation info;

	@Before
	public void setup() {
		info = new LegalInformation(USER, JUSTIFICATION, LOWER_BOUND, UPPER_BOUND);
	}

	@Test
	public void shouldTrimUser() throws IOException {
		final LegalInformation info = new LegalInformation(" " + USER + " \n", JUSTIFICATION, LOWER_BOUND, UPPER_BOUND);
		assertEquals(USER, info.user);
	}

	@Test
	public void shouldTrimJustification() throws IOException {
		final LegalInformation info = new LegalInformation(USER, " " + JUSTIFICATION + " \n", LOWER_BOUND, UPPER_BOUND);
		assertEquals(JUSTIFICATION, info.justification);
	}

	@Test
	public void shouldMakeIdenticalCopyUsingConstructor() throws IOException {
		final LegalInformation copy = new LegalInformation(info);
		assertEquals(info, copy);
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(info);
	}

	@Test
	public void shouldSurviveNullParameters() {
		assertCanSerializeAndDeserializeWithNullValues(info);
	}

	@Test
	public void shouldMatchParameterGroup() {
		assertParametersMatchParameterGroups(info);
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		assertHashCodeIsValidIncludingNullChecks(info);
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		assertEqualsIsValidIncludingNullChecks(info);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(info);
	}

	private static final String USER = "USER";
	private static final String JUSTIFICATION = "JUSTIFICATION";
	private static final Long LOWER_BOUND = 3l;
	private static final Long UPPER_BOUND = 4l;
}