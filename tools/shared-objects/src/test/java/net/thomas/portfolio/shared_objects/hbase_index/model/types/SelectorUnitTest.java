package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.serializeDeserialize;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;

import org.junit.Test;

public class SelectorUnitTest {
	@Test
	public void shouldSerializeAndDeserializeSimpleSelectorCorrectly() {
		final Selector deserializedInstance = serializeDeserialize(SELECTOR, Selector.class);
		assertEquals(SELECTOR, deserializedInstance);
	}

	private static final Selector SELECTOR;

	static {
		SELECTOR = buildSimpleSelector();
	}

	private static Selector buildSimpleSelector() {
		return new Selector(new DataTypeId("SimpleType", "ABCD01"), new LinkedHashMap<>());
	}
}