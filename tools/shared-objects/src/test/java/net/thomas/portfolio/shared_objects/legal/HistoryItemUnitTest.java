package net.thomas.portfolio.shared_objects.legal;

import static java.lang.System.currentTimeMillis;
import static net.thomas.portfolio.shared_objects.legal.LegalQueryType.SELECTOR_STATISTICS;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.HistoryItem.HistoryItemBuilder;

public class HistoryItemUnitTest {
	private final int SOME_ITEM_ID = 1;
	private HistoryItemBuilder builder;
	private DataTypeId dataTypeIdStub;
	private LegalInformation legalInformationStub;

	@Before
	public void setUp() {
		dataTypeIdStub = new DataTypeId();
		legalInformationStub = new LegalInformation();
		builder = HistoryItem.builder().type(SELECTOR_STATISTICS).selectorId(dataTypeIdStub).legalInfo(legalInformationStub).itemId(SOME_ITEM_ID);
	}

	@Test
	public void shouldUseNowAsTimeOfLogging() {
		final HistoryItem item = builder.build();
		assertEquals(currentTimeMillis(), item.getTimeOfLogging(), 10);
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(builder.build());
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(builder.build());
	}
}