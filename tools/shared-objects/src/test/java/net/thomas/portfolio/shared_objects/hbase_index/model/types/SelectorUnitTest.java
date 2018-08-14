package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;

import java.util.LinkedHashMap;

import org.junit.Test;

public class SelectorUnitTest {

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(SELECTOR);
	}

	private static final Selector SELECTOR;

	static {
		SELECTOR = buildSimpleSelector();
	}

	private static Selector buildSimpleSelector() {
		return new Selector(new DataTypeId("SimpleType", "ABCD01"), new LinkedHashMap<>());
	}
}