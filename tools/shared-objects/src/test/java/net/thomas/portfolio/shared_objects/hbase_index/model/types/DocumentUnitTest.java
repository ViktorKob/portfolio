package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.util.Collections.singletonMap;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.serializeDeserialize;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DocumentUnitTest {

	@Test
	public void shouldInitializeWithId() {
		final Document document = new Document(SOME_ID);
		assertEquals(SOME_ID, document.getId());
	}

	@Test
	public void shouldInitializeWithIdAndFields() {
		final Document document = new Document(SOME_ID, singletonMap(SOME_FIELD, SOME_VALUE));
		assertEquals(SOME_VALUE, document.get(SOME_FIELD));
	}

	@Test
	public void shouldContainCorrectTimeOfEvent() {
		final Document deserializedInstance = serializeDeserialize(SOME_DOCUMENT);
		assertEquals(SOME_DOCUMENT.getTimeOfEvent(), deserializedInstance.getTimeOfEvent());
	}

	@Test
	public void shouldContainCorrectTimeOfInterception() {
		final Document deserializedInstance = serializeDeserialize(SOME_DOCUMENT);
		assertEquals(SOME_DOCUMENT.getTimeOfInterception(), deserializedInstance.getTimeOfInterception());
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(SOME_DOCUMENT);
	}

	private static final DataTypeId SOME_ID = new DataTypeId("TYPE", "ABDC06");
	private static final String SOME_FIELD = "SomeField";
	private static final String SOME_VALUE = "SomeValue";
	private static final Document SOME_DOCUMENT = createSomeDocument();

	private static Document createSomeDocument() {
		final Document document = new Document();
		document.setId(SOME_ID);
		document.setTimeOfEvent(new Timestamp(1L));
		document.setTimeOfInterception(new Timestamp(2L));
		return document;
	}
}
