package net.thomas.portfolio.nexus.graphql.data_proxies;

import static net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp.UNKNOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class DocumentEntityProxyUnitTest {
	private DocumentEntityProxy proxy;

	@Before
	public void setUpForTest() {
		proxy = new DocumentEntityProxy(SOME_ENTITY, ADAPTORS_ARE_IGNORED);
	}

	@Test
	public void shouldContainId() {
		assertSame(SOME_ID, proxy.getId());
	}

	@Test
	public void shouldContainEntity() {
		assertSame(SOME_ENTITY, proxy.getEntity());
	}

	@Test
	public void shouldReturnUnknownTimeOfEventWhenEntityIsMissing() {
		proxy = new DocumentEntityProxy(null, ADAPTORS_ARE_IGNORED);
		assertEquals(UNKNOWN, proxy.getTimeOfEvent());
	}

	@Test
	public void shouldGetTimeOfEventFromEntity() {
		SOME_ENTITY.setTimeOfEvent(SOME_TIMESTAMP);
		assertEquals(SOME_TIMESTAMP, proxy.getTimeOfEvent());
	}

	@Test
	public void shouldReturnUnknownTimeOfInterceptionWhenEntityIsMissing() {
		proxy = new DocumentEntityProxy(null, ADAPTORS_ARE_IGNORED);
		assertEquals(UNKNOWN, proxy.getTimeOfInterception());
	}

	@Test
	public void shouldGetTimeOfInterceptionFromEntity() {
		SOME_ENTITY.setTimeOfInterception(SOME_TIMESTAMP);
		assertEquals(SOME_TIMESTAMP, proxy.getTimeOfInterception());
	}

	@Test
	public void shouldWorkWithParentBasedConstructor() {
		proxy = new DocumentEntityProxy(proxy, SOME_ENTITY, ADAPTORS_ARE_IGNORED);
		assertSame(SOME_ID, proxy.getId());
		assertSame(SOME_ENTITY, proxy.getEntity());
	}

	private static final DataTypeId SOME_ID = new DataTypeId("SomeType", "AA01");
	private static final Timestamp SOME_TIMESTAMP = new Timestamp(1000L);
	private final Document SOME_ENTITY = new Document(SOME_ID);
	private static final Adaptors ADAPTORS_ARE_IGNORED = null;
}