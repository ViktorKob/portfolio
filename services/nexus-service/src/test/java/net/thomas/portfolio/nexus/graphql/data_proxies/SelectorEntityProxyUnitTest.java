package net.thomas.portfolio.nexus.graphql.data_proxies;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SelectorEntityProxyUnitTest {
	private SelectorEntityProxy proxy;

	@Before
	public void setUpForTest() {
		proxy = new SelectorEntityProxy(SOME_ENTITY, ADAPTORS_ARE_IGNORED);
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
	public void shouldWorkWithParentBasedConstructor() {
		proxy = new SelectorEntityProxy(proxy, SOME_ENTITY, ADAPTORS_ARE_IGNORED);
		assertSame(SOME_ID, proxy.getId());
		assertSame(SOME_ENTITY, proxy.getEntity());
	}

	private static final DataTypeId SOME_ID = new DataTypeId("SomeType", "AA01");
	private final Selector SOME_ENTITY = new Selector(SOME_ID);
	private static final Adaptors ADAPTORS_ARE_IGNORED = null;
}
