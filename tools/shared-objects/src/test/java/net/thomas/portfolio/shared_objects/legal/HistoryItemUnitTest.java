package net.thomas.portfolio.shared_objects.legal;

import static java.lang.System.currentTimeMillis;
import static net.thomas.portfolio.shared_objects.legal.LegalQueryType.SELECTOR_STATISTICS;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public void shouldSerializeDeserializeCorrectly() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		final HistoryItem item = builder.build();
		final ObjectMapper mapper = new ObjectMapper();
		final HistoryItem actualValue = mapper.readValue(mapper.writeValueAsString(item), HistoryItem.class);
		assertEquals(item, actualValue);
	}
}
