package net.thomas.portfolio.hbase_index.schema;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertToStringIsValid;

import org.junit.Test;

public class EntityIdUnitTest {

	@Test
	public void shouldHaveValidHashCodeFunction() {
		assertHashCodeIsValidIncludingNullChecks(ENTITY_ID);
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		assertEqualsIsValidIncludingNullChecks(ENTITY_ID);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringIsValid(ENTITY_ID);
	}

	private static final String SOME_UID = "A0";
	private static final EntityId ENTITY_ID = new EntityId(Entity.class, SOME_UID);
}
