package net.thomas.portfolio.shared_objects.usage_data;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertParametersMatchParameterGroups;
import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.ANALYSED_DOCUMENT;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;

import org.junit.Before;
import org.junit.Test;

public class UsageActivityUnitTest {
	private UsageActivity activity;

	@Before
	public void setup() {
		activity = new UsageActivity(USER, ANALYSED_DOCUMENT, TIME_OF_ACTIVITY);
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(activity);
	}

	@Test
	public void shouldSurviveNullParameters() {
		assertCanSerializeAndDeserializeWithNullValues(activity);
	}

	@Test
	public void shouldMatchParameterGroup() {
		assertParametersMatchParameterGroups(activity);
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		assertHashCodeIsValidIncludingNullChecks(activity);
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		assertEqualsIsValidIncludingNullChecks(activity);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(activity);
	}

	private static final String USER = "USER";
	private static final Long TIME_OF_ACTIVITY = 1000l;
}