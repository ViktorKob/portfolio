package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.serializeDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DocumentInfoUnitTest {
	@Test
	public void shouldBeEqualToItself() {
		assertTrue(SOME_DOCUMENT_INFO.equals(SOME_DOCUMENT_INFO));
	}

	@Test
	public void shouldNotBeEqualToAnotherInstance() {
		assertFalse(SOME_DOCUMENT_INFO.equals(OTHER_DOCUMENT_INFO));
	}

	@Test
	public void shouldNotBeEqualToAnotherObject() {
		assertFalse(SOME_DOCUMENT_INFO.equals(OTHER_OBJECT));
	}

	@Test
	public void shouldContainCorrectTimeOfEvent() {
		final DocumentInfo deserializedInstance = serializeDeserialize(SOME_DOCUMENT_INFO);
		assertEquals(SOME_DOCUMENT_INFO.getTimeOfEvent(), deserializedInstance.getTimeOfEvent());
	}

	@Test
	public void shouldContainCorrectTimeOfInterception() {
		final DocumentInfo deserializedInstance = serializeDeserialize(SOME_DOCUMENT_INFO);
		assertEquals(SOME_DOCUMENT_INFO.getTimeOfInterception(), deserializedInstance.getTimeOfInterception());
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(SOME_DOCUMENT_INFO);
	}

	private static final Timestamp TIME_OF_EVENT = new Timestamp(1l);
	private static final Timestamp TIME_OF_INTERCEPTION = new Timestamp(2l);
	private static final DocumentInfo SOME_DOCUMENT_INFO;
	private static final DocumentInfo OTHER_DOCUMENT_INFO;
	private static final Object OTHER_OBJECT = "";

	static {
		SOME_DOCUMENT_INFO = new DocumentInfo(new DataTypeId("TYPE", "ABDC01"), TIME_OF_EVENT, TIME_OF_INTERCEPTION);
		OTHER_DOCUMENT_INFO = new DocumentInfo(new DataTypeId("TYPE", "ABDC02"), TIME_OF_EVENT, TIME_OF_INTERCEPTION);
	}
}