package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.Test;

public class IndexableFilterUnitTest {

	@Test
	public void shouldPickIndexableWithCorrectDocumentType() {
		IndexableFilter filter = new IndexableFilter.DataTypeFilter(new HashSet<>(asList(SOME_DOCUMENT_TYPE)));
		Collection<Indexable> filteredIndexables = filter.filter(INDEXABLES);
		assertSame(INDEXABLE_A, getOnlyElement(filteredIndexables));
	}

	@Test
	public void shouldPickIndexableWithCorrectPathType() {
		IndexableFilter filter = new IndexableFilter.RelationFilter(new HashSet<>(asList(SOME_PATH)));
		Collection<Indexable> filteredIndexables = filter.filter(INDEXABLES);
		assertSame(INDEXABLE_A, getOnlyElement(filteredIndexables));
	}

	private Indexable getOnlyElement(Collection<Indexable> indexables) {
		Iterator<Indexable> iterator = indexables.iterator();
		Indexable indexable = iterator.next();
		assertFalse("Collection was expected to only contain one element, but had " + indexables.size(),
				iterator.hasNext());
		return indexable;
	}

	private static final String SOME_PATH = "SOME_PATH";
	private static final String SOME_OTHER_PATH = "SOME_OTHER_PATH";
	private static final String SOME_DOCUMENT_TYPE = "SOME_DOCUMENT_TYPE";
	private static final String SOME_OTHER_DOCUMENT_TYPE = "SOME_OTHER_DOCUMENT_TYPE";
	private static final Indexable INDEXABLE_A = new Indexable("SELECTOR_TYPE", SOME_PATH, SOME_DOCUMENT_TYPE,
			"DOCUMENT_FIELD");
	private static final Indexable INDEXABLE_B = new Indexable("SELECTOR_TYPE", SOME_OTHER_PATH,
			SOME_OTHER_DOCUMENT_TYPE, "DOCUMENT_FIELD");
	private static final Collection<Indexable> INDEXABLES = asList(INDEXABLE_A, INDEXABLE_B);
}