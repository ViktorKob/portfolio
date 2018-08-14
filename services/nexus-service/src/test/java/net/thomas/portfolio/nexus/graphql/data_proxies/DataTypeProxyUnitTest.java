package net.thomas.portfolio.nexus.graphql.data_proxies;

import static net.thomas.portfolio.nexus.graphql.fetchers.GlobalServiceArgumentId.USER_ID;
import static net.thomas.portfolio.nexus.graphql.fetchers.LocalServiceArgumentId.JUSTIFICATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class DataTypeProxyUnitTest {

	private StubbedDataTypeProxy proxy;
	private Adaptors adaptors;

	@Before
	public void setUpForTest() {
		adaptors = new Adaptors.Builder().build();
		proxy = new StubbedDataTypeProxy(SOME_CONTENTS, adaptors);
		proxy.put(USER_ID, SOME_USER_ID);
		proxy.put(JUSTIFICATION, SOME_JUSTIFICATION);
	}

	@Test
	public void shouldLazilyGetEntityFromImplementation() {
		assertEquals(SOME_ENTITY, proxy.getEntity());
	}

	@Test
	public void shouldNotGetEntityFromImplementationTwice() {
		proxy.getEntity();
		proxy.getEntity(); // Stub throws RuntimeException if _getEntity is called twice
	}

	@Test
	public void shouldReturnIdFromImplementation() {
		assertEquals(SOME_DATA_TYPE_ID, proxy.getId());
	}

	@Test
	public void shouldHaveGlobalArgument() {
		assertEquals(SOME_USER_ID, proxy.get(USER_ID));
	}

	@Test
	public void shouldIgnoreNullGlobalArgument() {
		proxy.put(USER_ID, null);
		assertEquals(SOME_USER_ID, proxy.get(USER_ID));
	}

	@Test
	public void shouldHaveLocalArgument() {
		assertEquals(SOME_JUSTIFICATION, proxy.get(JUSTIFICATION));
	}

	@Test
	public void shouldIgnoreNullLocalArgument() {
		proxy.put(JUSTIFICATION, null);
		assertEquals(SOME_JUSTIFICATION, proxy.get(JUSTIFICATION));
	}

	@Test
	public void shouldPreserveGlobalArgumentWhenCreatingChild() {
		final StubbedDataTypeProxy childProxy = new StubbedDataTypeProxy(proxy, SOME_CONTENTS, adaptors);
		assertEquals(SOME_USER_ID, childProxy.get(USER_ID));
	}

	@Test
	public void shouldHaveGlobalArgumentsInToString() {
		final String asString = proxy.toString();
		assertTrue(asString.contains(USER_ID.name()) && asString.contains(SOME_USER_ID));
	}

	@Test
	public void shouldHaveLocalArgumentsInToString() {
		final String asString = proxy.toString();
		assertTrue(asString.contains(JUSTIFICATION.name()) && asString.contains(SOME_JUSTIFICATION));
	}

	@Test
	public void shouldShowEntityHasNotBeenLoadedInToString() {
		final String asString = proxy.toString();
		assertTrue(asString.contains("entity loaded: false"));
	}

	@Test
	public void shouldShowEntityHasBeenLoadedInToString() {
		proxy.getEntity();
		final String asString = proxy.toString();
		assertTrue(asString.contains("entity loaded: true"));
	}

	@Test
	public void shouldShowContentsAsStringInToString() {
		final String asString = proxy.toString();
		assertTrue(asString.contains(SOME_CONTENTS));
	}

	private static final String SOME_USER_ID = "UserId";
	private static final String SOME_JUSTIFICATION = "Justification";
	private static final String SOME_CONTENTS = "SOME_CONTENTS";
	private static final DataTypeId SOME_DATA_TYPE_ID = new DataTypeId("SomeType", "AA01");
	private static final DataType SOME_ENTITY = new DataType(SOME_DATA_TYPE_ID);

	class StubbedDataTypeProxy extends DataTypeProxy<Object, DataType> {
		private boolean getEntityHasBeenCalled;

		public StubbedDataTypeProxy(Object contents, Adaptors adaptors) {
			super(contents, adaptors);
			getEntityHasBeenCalled = false;
		}

		public StubbedDataTypeProxy(DataTypeProxy<?, ?> parent, Object contents, Adaptors adaptors) {
			super(parent, contents, adaptors);
			getEntityHasBeenCalled = false;
		}

		@Override
		public DataTypeId getId() {
			return SOME_DATA_TYPE_ID;
		}

		@Override
		protected DataType _getEntity() {
			if (getEntityHasBeenCalled) {
				throw new RuntimeException("_getEntity was called more than once");
			}
			getEntityHasBeenCalled = true;
			return SOME_ENTITY;
		}
	}
}
