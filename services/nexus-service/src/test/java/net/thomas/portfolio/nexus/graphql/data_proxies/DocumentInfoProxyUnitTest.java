package net.thomas.portfolio.nexus.graphql.data_proxies;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public class DocumentInfoProxyUnitTest {
	private DocumentInfoProxy proxy;
	private Adaptors adaptors;

	@Before
	public void setUpForTest() {
		adaptors = mock(Adaptors.class);
		proxy = new DocumentInfoProxy(SOME_ENTITY, adaptors);
	}

	@Test
	public void shouldContainId() {
		assertSame(SOME_ID, proxy.getId());
	}

	@Test
	public void shouldContainEntity() {
		proxy.getEntity();
		verify(adaptors, times(1)).getDataType(same(SOME_ID));
	}

	@Test
	public void shouldWorkWithParentBasedConstructor() {
		proxy = new DocumentInfoProxy(proxy, SOME_ENTITY, adaptors);
		assertSame(SOME_ID, proxy.getId());
		proxy.getEntity();
		verify(adaptors, times(1)).getDataType(same(SOME_ID));
	}

	private static final DataTypeId SOME_ID = new DataTypeId("SomeType", "AA01");
	private final DocumentInfo SOME_ENTITY = new DocumentInfo(SOME_ID, null, null);
}
