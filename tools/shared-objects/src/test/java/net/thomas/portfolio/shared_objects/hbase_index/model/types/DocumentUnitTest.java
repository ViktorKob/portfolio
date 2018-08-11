package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.serializeDeserialize;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DocumentUnitTest {

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

	private static final Document SOME_DOCUMENT;

	static {
		SOME_DOCUMENT = createSomeDocument();
	}

	private static Document createSomeDocument() {
		final Document document = new Document();
		document.setId(new DataTypeId("TYPE", "ABDC06"));
		document.setTimeOfEvent(new Timestamp(1l));
		document.setTimeOfInterception(new Timestamp(2l));
		return document;
	}
}
