package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;

import java.util.LinkedHashMap;

import org.junit.Test;

public class RawDataTypeUnitTest {

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(RAW_TYPE);
	}

	private static final RawDataType RAW_TYPE;

	static {
		RAW_TYPE = new RawDataType(new DataTypeId("RawType", "ABCD01"), new LinkedHashMap<>());
	}
}