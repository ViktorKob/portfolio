package net.thomas.portfolio.hbase_index.lookup;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.IndexableFilter;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.IndexableFilter.DataTypeFilter;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class InvertedIndexLookupAndBuilderUnitTest {
	private InvertedIndexLookupBuilder lookupBuilder;
	private HbaseIndex index;

	@Before
	public void setUpForTest() {
		index = mock(HbaseIndex.class);
		lookupBuilder = new InvertedIndexLookupBuilder(index, newSingleThreadExecutor());
		lookupBuilder.setSelectorId(SOME_SELECTOR_ID);
		lookupBuilder.setIndexables(asList(SOME_INDEXABLE, SOME_OTHER_INDEXABLE));
		when(index.invertedIndexLookup(eq(SOME_SELECTOR_ID), eq(SOME_INDEXABLE))).thenReturn(SOME_DOCUMENT_INFOS);
		when(index.invertedIndexLookup(eq(SOME_SELECTOR_ID), eq(SOME_OTHER_INDEXABLE))).thenReturn(SOME_OTHER_DOCUMENT_INFOS);
	}

	@Test
	public void shouldUseIdForLookup() {
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		indexLookup.execute();
		verify(index, TWICE).invertedIndexLookup(eq(SOME_SELECTOR_ID), any());
	}

	@Test
	public void shouldUseIndexablesForLookup() {
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		indexLookup.execute();
		verify(index, ONCE).invertedIndexLookup(any(), same(SOME_INDEXABLE));
		verify(index, ONCE).invertedIndexLookup(any(), same(SOME_OTHER_INDEXABLE));
	}

	@Test
	public void shouldIncludeIndexableWithCorrectDocumentType() {
		lookupBuilder.addIndexableFilter(new DataTypeFilter(newHashSet(SOME_DOCUMENT_TYPE)));
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		indexLookup.execute();
		verify(index, ONCE).invertedIndexLookup(any(), same(SOME_INDEXABLE));
	}

	@Test
	public void shouldRejectIndexableWithWrongDocumentType() {
		lookupBuilder.addIndexableFilter(new DataTypeFilter(newHashSet(SOME_OTHER_DOCUMENT_TYPE)));
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		indexLookup.execute();
		verify(index, never()).invertedIndexLookup(any(), same(SOME_INDEXABLE));
	}

	@Test
	public void shouldIncludeIndexableWithCorrectPath() {
		lookupBuilder.addIndexableFilter(new IndexableFilter.RelationFilter(newHashSet(SOME_PATH)));
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		indexLookup.execute();
		verify(index, ONCE).invertedIndexLookup(any(), same(SOME_INDEXABLE));
	}

	@Test
	public void shouldRejectIndexableWithWrongPath() {
		lookupBuilder.addIndexableFilter(new DataTypeFilter(newHashSet(SOME_OTHER_PATH)));
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		indexLookup.execute();
		verify(index, never()).invertedIndexLookup(any(), same(SOME_INDEXABLE));
	}

	@Test
	public void shouldIncludeAllResultsWithoutBounds() {
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		final DocumentInfos result = indexLookup.execute();

		assertContains(result, DOCUMENT_A, DOCUMENT_B, DOCUMENT_C, DOCUMENT_D, DOCUMENT_E);
	}

	@Test
	public void shouldUseOffsetBoundToLimitResult() {
		lookupBuilder.updateBounds(new Bounds(SOME_BOUNDS.offset, null, null, null));
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		final DocumentInfos result = indexLookup.execute();
		assertContains(result, DOCUMENT_A, DOCUMENT_B, DOCUMENT_C, DOCUMENT_D);
	}

	@Test
	public void shouldUseLimitBoundToLimitResult() {
		lookupBuilder.updateBounds(new Bounds(null, SOME_BOUNDS.limit, null, null));
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		final DocumentInfos result = indexLookup.execute();
		assertContains(result, DOCUMENT_B, DOCUMENT_C, DOCUMENT_D, DOCUMENT_E);
	}

	@Test
	public void shouldUseAfterBoundToLimitResult() {
		lookupBuilder.updateBounds(new Bounds(null, null, SOME_BOUNDS.after, null));
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		final DocumentInfos result = indexLookup.execute();
		assertContains(result, DOCUMENT_B, DOCUMENT_C, DOCUMENT_D, DOCUMENT_E);
	}

	@Test
	public void shouldUseBeforeBoundToLimitResult() {
		lookupBuilder.updateBounds(new Bounds(null, null, null, SOME_BOUNDS.before));
		final InvertedIndexLookup indexLookup = lookupBuilder.build();
		final DocumentInfos result = indexLookup.execute();
		assertContains(result, DOCUMENT_A, DOCUMENT_B, DOCUMENT_C, DOCUMENT_D);
	}

	private void assertContains(final DocumentInfos result, DocumentInfo... documents) {
		for (final DocumentInfo document : documents) {
			assertTrue("Could not find " + document.getId() + " in result " + result, result.getInfos()
				.contains(document));
		}
	}

	private static final VerificationMode ONCE = times(1);
	private static final VerificationMode TWICE = times(2);
	private static final String SOME_SELECTOR_TYPE = "SomeSelectorType";
	private static final String SOME_DOCUMENT_TYPE = "SomeDocumentType";
	private static final String SOME_OTHER_DOCUMENT_TYPE = "SomeOtherDocumentType";
	private static final String SOME_PATH = "SomePath";
	private static final String SOME_OTHER_PATH = "SomeOtherPath";
	private static final DataTypeId SOME_SELECTOR_ID = new DataTypeId(SOME_SELECTOR_TYPE, "AABBB0011");
	private static final Indexable SOME_INDEXABLE = new Indexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, "SomeField");
	private static final Indexable SOME_OTHER_INDEXABLE = new Indexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_OTHER_DOCUMENT_TYPE, "SomeField");
	private static final Bounds SOME_BOUNDS = new Bounds(1, 4, 2000L, 4000L);
	private static final DocumentInfo DOCUMENT_A = new DocumentInfo(new DataTypeId(SOME_DOCUMENT_TYPE, "A1"), new Timestamp(1000L), new Timestamp(1002L));
	private static final DocumentInfo DOCUMENT_B = new DocumentInfo(new DataTypeId(SOME_DOCUMENT_TYPE, "A2"), new Timestamp(2000L), new Timestamp(2002L));
	private static final DocumentInfo DOCUMENT_D = new DocumentInfo(new DataTypeId(SOME_OTHER_DOCUMENT_TYPE, "B1"), new Timestamp(3000L), new Timestamp(3002L));
	private static final DocumentInfo DOCUMENT_C = new DocumentInfo(new DataTypeId(SOME_DOCUMENT_TYPE, "A3"), new Timestamp(4000L), new Timestamp(4002L));
	private static final DocumentInfo DOCUMENT_E = new DocumentInfo(new DataTypeId(SOME_OTHER_DOCUMENT_TYPE, "B2"), new Timestamp(5000L), new Timestamp(5002L));
	private static final DocumentInfos SOME_DOCUMENT_INFOS = new DocumentInfos(asList(DOCUMENT_A, DOCUMENT_B, DOCUMENT_C));
	private static final DocumentInfos SOME_OTHER_DOCUMENT_INFOS = new DocumentInfos(asList(DOCUMENT_D, DOCUMENT_E));
}