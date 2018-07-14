package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class SimpleRepresentationParserLibraryBuilderUnitTest {

	private SimpleRepresentationParserLibraryBuilder builder;
	private SimpleRepresentationParser parserMock;

	@Before
	public void setUpForTest() {
		builder = new SimpleRepresentationParserLibraryBuilder();
		parserMock = mock(SimpleRepresentationParser.class);
		when(parserMock.getType()).thenReturn(TYPE);
	}

	@Test(expected = RuntimeException.class)
	public void shouldRejectAddingSameParserTwice() {
		builder.add(parserMock);
		builder.add(parserMock);
	}

	private static final String TYPE = "TYPE";
}