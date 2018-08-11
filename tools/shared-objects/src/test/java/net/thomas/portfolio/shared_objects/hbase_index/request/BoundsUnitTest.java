package net.thomas.portfolio.shared_objects.hbase_index.request;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertParametersMatchParameterGroups;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertToStringIsValid;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class BoundsUnitTest {

	private Bounds bounds;

	@Before
	public void setup() {
		bounds = new Bounds(SOME_OFFSET, SOME_LIMIT, SOME_AFTER, SOME_BEFORE);
	}

	@Test
	public void shouldMakeIdenticalCopyUsingConstructor() throws IOException {
		final Bounds copy = new Bounds(bounds);
		assertEquals(bounds, copy);
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(bounds);
	}

	@Test
	public void shouldSurviveNullParameters() {
		assertCanSerializeAndDeserializeWithNullValues(bounds);
	}

	@Test
	public void shouldMatchParameterGroup() {
		assertParametersMatchParameterGroups(bounds);
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		assertHashCodeIsValidIncludingNullChecks(bounds);
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		assertEqualsIsValidIncludingNullChecks(bounds);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringIsValid(bounds);
	}

	private static final Integer SOME_OFFSET = 1;
	private static final Integer SOME_LIMIT = 2;
	private static final Long SOME_AFTER = 3l;
	private static final Long SOME_BEFORE = 4l;
}
