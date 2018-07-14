package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.serializeDeserialize;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;

import org.junit.Test;

public class RawDataTypeUnitTest {
	@Test
	public void shouldDeserializeEndpointCorrectly() {
		final RawDataType deserializedInstance = serializeDeserialize(RAW_TYPE, RawDataType.class);
		assertEquals(RAW_TYPE, deserializedInstance);
	}

	private static final RawDataType RAW_TYPE;

	static {
		RAW_TYPE = new RawDataType(new DataTypeId("RawType", "ABCD01"), new LinkedHashMap<>());
	}
}