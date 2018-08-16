package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.UID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataTypeIdProxy;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;

public class DataTypeFetcherUnitTest {
	private DataFetchingEnvironment environment;
	private DataTypeFetcher fetcher;

	@Before
	public void setUpForTest() {
		Adaptors adaptors = new Adaptors.Builder().build();
		environment = mock(DataFetchingEnvironment.class);
		fetcher = new DataTypeFetcher(SOME_TYPE, adaptors);
		when(environment.getArgument(eq(UID.getName()))).thenReturn(SOME_UID);
	}

	@Test
	public void shouldContainUid() {
		when(environment.containsArgument(eq(UID.getName()))).thenReturn(true);
		DataTypeIdProxy proxy = fetcher.get(environment);
		assertEquals(SOME_UID, proxy.getId().uid);
	}

	@Test
	public void shouldContainType() {
		when(environment.containsArgument(eq(UID.getName()))).thenReturn(true);
		DataTypeIdProxy proxy = fetcher.get(environment);
		assertEquals(SOME_TYPE, proxy.getId().type);
	}

	@Test
	public void shouldReturnNullWhenUidIsMissing() {
		when(environment.containsArgument(eq(UID.getName()))).thenReturn(false);
		assertNull(fetcher.get(environment));
	}

	private static final String SOME_TYPE = "SomeType";
	private static final String SOME_UID = "AA00";
}