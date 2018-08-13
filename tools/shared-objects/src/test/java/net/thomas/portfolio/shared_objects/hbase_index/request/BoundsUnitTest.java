package net.thomas.portfolio.shared_objects.hbase_index.request;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertParametersMatchParameterGroups;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
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
	public void shouldOverwriteOffset() {
		final Bounds bounds = new Bounds();
		bounds.update(new Bounds(SOME_OFFSET, null, null, null));
		assertEquals(SOME_OFFSET, bounds.offset);
	}

	@Test
	public void shouldOverwriteLimit() {
		final Bounds bounds = new Bounds();
		bounds.update(new Bounds(null, SOME_LIMIT, null, null));
		assertEquals(SOME_LIMIT, bounds.limit);
	}

	@Test
	public void shouldOverwriteAfter() {
		final Bounds bounds = new Bounds();
		bounds.update(new Bounds(null, null, SOME_AFTER, null));
		assertEquals(SOME_AFTER, bounds.after);
	}

	@Test
	public void shouldOverwriteBefore() {
		final Bounds bounds = new Bounds();
		bounds.update(new Bounds(null, null, null, SOME_BEFORE));
		assertEquals(SOME_BEFORE, bounds.before);
	}

	@Test
	public void shouldReplaceOffset() {
		final Bounds bounds = new Bounds(null, SOME_LIMIT, SOME_AFTER, SOME_BEFORE);
		bounds.replaceMissing(SOME_OFFSET, null, null, null);
		assertEquals(SOME_OFFSET, bounds.offset);
	}

	@Test
	public void shouldReplaceLimit() {
		final Bounds bounds = new Bounds(SOME_OFFSET, null, SOME_AFTER, SOME_BEFORE);
		bounds.replaceMissing(null, SOME_LIMIT, null, null);
		assertEquals(SOME_LIMIT, bounds.limit);
	}

	@Test
	public void shouldReplaceAfter() {
		final Bounds bounds = new Bounds(SOME_OFFSET, SOME_LIMIT, null, SOME_BEFORE);
		bounds.replaceMissing(null, null, SOME_AFTER, null);
		assertEquals(SOME_AFTER, bounds.after);
	}

	@Test
	public void shouldReplaceAfterWithNewerAfter() {
		final Bounds bounds = new Bounds(SOME_OFFSET, SOME_LIMIT, SOME_AFTER, SOME_BEFORE);
		bounds.replaceMissing(null, null, SOME_AFTER + 1, null);
		assertEquals((Long) (SOME_AFTER + 1), bounds.after);
	}

	@Test
	public void shouldNotReplaceAfterWithOlderAfter() {
		final Bounds bounds = new Bounds(SOME_OFFSET, SOME_LIMIT, SOME_AFTER, SOME_BEFORE);
		bounds.replaceMissing(null, null, SOME_AFTER - 1, null);
		assertEquals(SOME_AFTER, bounds.after);
	}

	@Test
	public void shouldReplaceBefore() {
		final Bounds bounds = new Bounds(SOME_OFFSET, SOME_LIMIT, SOME_AFTER, null);
		bounds.replaceMissing(null, null, null, SOME_BEFORE);
		assertEquals(SOME_BEFORE, bounds.before);
	}

	@Test
	public void shouldReplaceBeforeWithOlderBefore() {
		final Bounds bounds = new Bounds(SOME_OFFSET, SOME_LIMIT, SOME_AFTER, SOME_BEFORE);
		bounds.replaceMissing(null, null, null, SOME_BEFORE - 1);
		assertEquals((Long) (SOME_BEFORE - 1), bounds.before);
	}

	@Test
	public void shouldNotReplaceBeforeWithNewerBefore() {
		final Bounds bounds = new Bounds(SOME_OFFSET, SOME_LIMIT, SOME_AFTER, SOME_BEFORE);
		bounds.replaceMissing(null, null, null, SOME_BEFORE + 1);
		assertEquals(SOME_BEFORE, bounds.before);
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
		assertToStringContainsAllFieldsFromObject(bounds);
	}

	private static final Integer SOME_OFFSET = 1;
	private static final Integer SOME_LIMIT = 2;
	private static final Long SOME_AFTER = 3l;
	private static final Long SOME_BEFORE = 4l;
}
