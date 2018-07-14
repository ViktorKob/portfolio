package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.serializeDeserialize;
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
	public void shouldSerializeDeserializeDocumentInfo() {
		final DocumentInfo deserializedInstance = serializeDeserialize(SOME_DOCUMENT_INFO, DocumentInfo.class);
		assertEquals(SOME_DOCUMENT_INFO, deserializedInstance);
	}

	@Test
	public void shouldContainCorrectTimeOfEvent() {
		final DocumentInfo deserializedInstance = serializeDeserialize(SOME_DOCUMENT_INFO, DocumentInfo.class);
		assertEquals(SOME_DOCUMENT_INFO.getTimeOfEvent(), deserializedInstance.getTimeOfEvent());
	}

	@Test
	public void shouldContainCorrectTimeOfInterception() {
		final DocumentInfo deserializedInstance = serializeDeserialize(SOME_DOCUMENT_INFO, DocumentInfo.class);
		assertEquals(SOME_DOCUMENT_INFO.getTimeOfInterception(), deserializedInstance.getTimeOfInterception());
	}

	private static final long TIME_OF_EVENT = 1l;
	private static final long TIME_OF_INTERCEPTION = 2l;
	private static final DocumentInfo SOME_DOCUMENT_INFO;
	private static final DocumentInfo OTHER_DOCUMENT_INFO;
	private static final Object OTHER_OBJECT = "";

	static {
		SOME_DOCUMENT_INFO = new DocumentInfo(new DataTypeId("TYPE", "ABDC01"), TIME_OF_EVENT, TIME_OF_INTERCEPTION);
		OTHER_DOCUMENT_INFO = new DocumentInfo(new DataTypeId("TYPE", "ABDC02"), TIME_OF_EVENT, TIME_OF_INTERCEPTION);
	}
}