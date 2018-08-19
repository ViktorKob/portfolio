package net.thomas.portfolio.hbase_index.fake;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.EMAIL_ENDPOINT_MISSING_ADDRESS;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.REFERENCES_FOR_SOME_EMAIL;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.SOME_EMAIL;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.SOME_EMAIL_ENDPOINT;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.SOME_LOCALNAME;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.SOME_OTHER_EMAIL;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.getClassSimpleName;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.idFor;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.fake.world.WorldAccess;
import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.EntityId;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.data.InvertedIndex;
import net.thomas.portfolio.hbase_index.schema.processing.data.SelectorStatistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;

public class FakeHbaseIndexUnitTest {
	private WorldAccess world;
	private FakeHbaseIndex index;
	private InvertedIndex invertedIndex;
	private SelectorStatistics statistics;

	@Before
	public void setUpForTest() {
		world = mock(WorldAccess.class);
		when(world.iterator()).thenReturn(iteratorWith(SOME_EMAIL));
		when(world.getEvent(eq(SOME_EMAIL.uid))).thenReturn(SOME_EMAIL);
		invertedIndex = mock(InvertedIndex.class);
		statistics = mock(SelectorStatistics.class);
		index = new FakeHbaseIndex();
		index.setWorldAccess(world);
		index.setInvertedIndex(invertedIndex);
		index.setSelectorStatistics(statistics);
	}

	@Test
	public void shouldContainEntityAsDataType() {
		final DataType email = index.getDataType(idFor(SOME_EMAIL));
		assertEquals(SOME_EMAIL.uid, email.getId().uid);
	}

	@Test
	public void shouldReturnNullWhenDataTypeNotPresent() {
		assertNull(index.getDataType(idFor(SOME_OTHER_EMAIL)));
	}

	@Test
	public void shouldReturnNullWhenIdNotPresent() {
		assertNull(index.getDataType(idFor(SOME_OTHER_EMAIL)));
	}

	@Test
	public void shouldContainChildOfEntityAsDataType() {
		final DataType fromEndpoint = index.getDataType(idFor(SOME_EMAIL.from));
		assertEquals(SOME_EMAIL.from.uid, fromEndpoint.getId().uid);
	}

	@Test
	public void shouldNotReturnEventAsSample() {
		final Entities entities = index.getSamples(getClassSimpleName(SOME_EMAIL), 1);
		assertEquals(0, entities.getEntities().size());
	}

	@Test
	public void shouldPickSampleAtRandomWhenPossible() {
		final Entities entities = index.getSamples(getClassSimpleName(SOME_EMAIL.from), 1);
		final String sampleUid = getFirst(entities).getId().uid;
		assertTrue(SOME_EMAIL_ENDPOINT.uid.equals(sampleUid) || EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME.uid.equals(sampleUid)
				|| EMAIL_ENDPOINT_MISSING_ADDRESS.uid.equals(sampleUid));
	}

	@Test
	public void shouldReturnEmptyEntitiesWhenNoSamplesPresent() {
		final Entities entities = index.getSamples(getClassSimpleName(SOME_EMAIL), 1);
		assertTrue(entities.getEntities().isEmpty());
	}

	@Test
	public void shouldContainReferencesWhenAdded() {
		when(world.getReferences(eq(SOME_EMAIL.uid))).thenReturn(REFERENCES_FOR_SOME_EMAIL);
		final References references = index.getReferences(idFor(SOME_EMAIL));
		assertSame(REFERENCES_FOR_SOME_EMAIL, references);
	}

	@Test
	public void shouldLookupSelectorInInvertedIndex() {
		when(invertedIndex.getEventUids(eq(SOME_LOCALNAME.uid), any())).thenReturn(singletonList(entityIdFor(SOME_EMAIL)));
		final DocumentInfos infos = index.invertedIndexLookup(idFor(SOME_LOCALNAME), new StubbedIndexable());
		assertEquals(SOME_EMAIL.uid, getFirst(infos).getId().uid);
	}

	@Test
	public void shouldLookupSelectorStatistics() {
		when(statistics.get(eq(SOME_LOCALNAME.uid))).thenReturn(singletonMap(INFINITY, 1l));
		final Statistics statistics = index.getStatistics(idFor(SOME_LOCALNAME));
		assertEquals(SOME_COUNT, (long) statistics.get(INFINITY));
	}

	// TODO[Thomas]: Consider overriding System.out and check contents
	@Test
	public void shouldPrintSamplesWhenAsked() {
		index.printSamples(10);
	}

	private static final long SOME_COUNT = 1l;

	private Iterator<Event> iteratorWith(final Event... events) {
		return asList(events).iterator();
	}

	private EntityId entityIdFor(final Entity entity) {
		return new EntityId(entity.getClass(), entity.uid);
	}

	private DataType getFirst(final Entities entities) {
		return entities.getEntities().iterator().next();
	}

	private DocumentInfo getFirst(final DocumentInfos infos) {
		return infos.getInfos().iterator().next();
	}

	class StubbedIndexable extends Indexable {
		@Override
		public String getPath() {
			return "";
		}
	}
}
