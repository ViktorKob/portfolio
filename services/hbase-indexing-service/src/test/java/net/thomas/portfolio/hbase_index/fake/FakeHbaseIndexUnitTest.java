package net.thomas.portfolio.hbase_index.fake;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.REFERENCES_FOR_SOME_EMAIL;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.SOME_EMAIL;
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

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.EntityId;
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
	private FakeHbaseIndex index;
	private InvertedIndex invertedIndex;
	private SelectorStatistics statistics;

	@Before
	public void setUpForTest() {
		invertedIndex = mock(InvertedIndex.class);
		statistics = mock(SelectorStatistics.class);
		index = new FakeHbaseIndex();
		index.setInvertedIndex(invertedIndex);
		index.setSelectorStatistics(statistics);
	}

	@Test
	public void shouldContainEntityAsDataType() {
		index.addEntitiesAndChildren(singleton(SOME_EMAIL));
		DataType email = index.getDataType(idFor(SOME_EMAIL));
		assertEquals(SOME_EMAIL.uid, email.getId().uid);
	}

	@Test
	public void shouldReturnNullWhenDataTypeNotPresent() {
		assertNull(index.getDataType(idFor(SOME_OTHER_EMAIL)));
	}

	@Test
	public void shouldReturnNullWhenIdNotPresent() {
		index.addEntitiesAndChildren(singleton(SOME_EMAIL));
		assertNull(index.getDataType(idFor(SOME_OTHER_EMAIL)));
	}

	@Test
	public void shouldContainChildOfEntityAsDataType() {
		index.addEntitiesAndChildren(singleton(SOME_EMAIL));
		DataType fromEndpoint = index.getDataType(idFor(SOME_EMAIL.from));
		assertEquals(SOME_EMAIL.from.uid, fromEndpoint.getId().uid);
	}

	@Test
	public void shouldReturnEntityAsSample() {
		index.addEntitiesAndChildren(singleton(SOME_EMAIL));
		Entities entities = index.getSamples(getClassSimpleName(SOME_EMAIL), 1);
		assertEquals(SOME_EMAIL.uid, getFirst(entities).getId().uid);
	}

	@Test
	public void shouldPickSampleAtRandomWhenPossible() {
		index.addEntitiesAndChildren(asList(SOME_EMAIL, SOME_OTHER_EMAIL));
		Entities entities = index.getSamples(getClassSimpleName(SOME_EMAIL), 1);
		String sampleUid = getFirst(entities).getId().uid;
		assertTrue(SOME_EMAIL.uid.equals(sampleUid) || SOME_OTHER_EMAIL.uid.equals(sampleUid));
	}

	@Test
	public void shouldReturnEmptyEntitiesWhenNoSamplesPresent() {
		Entities entities = index.getSamples(getClassSimpleName(SOME_EMAIL), 1);
		assertTrue(entities.getEntities().isEmpty());
	}

	@Test
	public void shouldContainReferencesWhenAdded() {
		index.setReferences(singletonMap(SOME_EMAIL.uid, REFERENCES_FOR_SOME_EMAIL));
		References references = index.getReferences(idFor(SOME_EMAIL));
		assertSame(REFERENCES_FOR_SOME_EMAIL, references);
	}

	@Test
	public void shouldLookupSelectorInInvertedIndex() {
		when(invertedIndex.getEventUids(eq(SOME_LOCALNAME.uid), any()))
				.thenReturn(singletonList(entityIdFor(SOME_EMAIL)));
		index.addEntitiesAndChildren(singleton(SOME_EMAIL));
		DocumentInfos infos = index.invertedIndexLookup(idFor(SOME_LOCALNAME), new StubbedIndexable());
		assertEquals(SOME_EMAIL.uid, getFirst(infos).getId().uid);
	}

	@Test
	public void shouldLookupSelectorStatistics() {
		when(statistics.get(eq(SOME_LOCALNAME.uid))).thenReturn(singletonMap(INFINITY, 1l));
		index.addEntitiesAndChildren(singleton(SOME_EMAIL));
		Statistics statistics = index.getStatistics(idFor(SOME_LOCALNAME));
		assertEquals(SOME_COUNT, (long) statistics.get(INFINITY));
	}

	@Test
	public void shouldContainIndexEntitiesInSerializable() {
		index.addEntitiesAndChildren(singleton(SOME_EMAIL));
		index = serializeDeserialize(index);
		assertEquals(SOME_EMAIL.uid, index.getDataType(idFor(SOME_EMAIL)).getId().uid);
	}

	@Test
	public void shouldContainReferencesAfterSerialization() {
		index.setReferences(singletonMap(SOME_EMAIL.uid, REFERENCES_FOR_SOME_EMAIL));
		References references = serializeDeserialize(index).getReferences(idFor(SOME_EMAIL));
		assertEquals(REFERENCES_FOR_SOME_EMAIL, references);
	}

	@Test
	public void shouldLookupSelectorInInvertedIndexAfterSerialization() {
		when(invertedIndex.getEventUids(eq(SOME_LOCALNAME.uid), any()))
				.thenReturn(singletonList(entityIdFor(SOME_EMAIL)));
		index.addEntitiesAndChildren(singleton(SOME_EMAIL));
		DocumentInfos infos = serializeDeserialize(index).invertedIndexLookup(idFor(SOME_LOCALNAME),
				new StubbedIndexable());
		assertEquals(SOME_EMAIL.uid, getFirst(infos).getId().uid);
	}

	@Test
	public void shouldLookupSelectorStatisticsAfterSerialization() {
		when(statistics.get(eq(SOME_LOCALNAME.uid))).thenReturn(singletonMap(INFINITY, 1l));
		index.addEntitiesAndChildren(singleton(SOME_EMAIL));
		Statistics statistics = serializeDeserialize(index).getStatistics(idFor(SOME_LOCALNAME));
		assertEquals(SOME_COUNT, (long) statistics.get(INFINITY));
	}

	private FakeHbaseIndex serializeDeserialize(FakeHbaseIndex index) {
		return new FakeHbaseIndex(index.getSerializable());
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.configure(FAIL_ON_EMPTY_BEANS, false);
//		try {
//			String serialized = mapper.writeValueAsString(index.getSerializable());
//			return new FakeHbaseIndex(mapper.readValue(serialized, FakeHbaseIndexSerializable.class));
//		} catch (IOException e) {
//			throw new RuntimeException("Unable to serialize / deserialize index", e);
//		}
	}

	// TODO[Thomas]: Consider overriding System.out and check contents
	@Test
	public void shouldPrintSamplesWhenAsked() {
		index.printSamples(10);
	}

	private static final long SOME_COUNT = 1l;

	private EntityId entityIdFor(Entity entity) {
		return new EntityId(entity.getClass(), entity.uid);
	}

	private DataType getFirst(Entities entities) {
		return entities.getEntities().iterator().next();
	}

	private DocumentInfo getFirst(DocumentInfos infos) {
		return infos.getInfos().iterator().next();
	}

	class StubbedIndexable extends Indexable {
		@Override
		public String getPath() {
			return "";
		}
	}
}
