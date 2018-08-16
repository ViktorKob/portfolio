package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.UID;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.USER;
import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalServiceArgumentId.USER_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentProxy;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;

public class DocumentFetcherUnitTest {
	private DataFetchingEnvironment environment;
	private DocumentFetcher fetcher;

	@Before
	public void setUpForTest() {
		Adaptors adaptors = new Adaptors.Builder().build();
		environment = mock(DataFetchingEnvironment.class);
		fetcher = new DocumentFetcher(SOME_TYPE, adaptors);
		when(environment.getArgument(eq(UID.getName()))).thenReturn(SOME_UID);
		when(environment.getArgument(eq(USER.getName()))).thenReturn(SOME_USER);
	}

	@Test
	public void shouldContainUid() {
		when(environment.containsArgument(eq(UID.getName()))).thenReturn(true);
		DocumentProxy<?> proxy = fetcher.get(environment);
		assertEquals(SOME_UID, proxy.getId().uid);
	}

	@Test
	public void shouldContainType() {
		when(environment.containsArgument(eq(UID.getName()))).thenReturn(true);
		DocumentProxy<?> proxy = fetcher.get(environment);
		assertEquals(SOME_TYPE, proxy.getId().type);
	}

	@Test
	public void shouldContainUserId() {
		when(environment.containsArgument(eq(UID.getName()))).thenReturn(true);
		DocumentProxy<?> proxy = fetcher.get(environment);
		assertEquals(SOME_USER, proxy.get(USER_ID));
	}

	@Test
	public void shouldReturnNullWhenUidIsMissing() {
		when(environment.containsArgument(eq(UID.getName()))).thenReturn(false);
		assertNull(fetcher.get(environment));
	}

	private static final String SOME_TYPE = "SomeType";
	private static final String SOME_UID = "AA00";
	private static final String SOME_USER = "SomeUser";
}