package net.thomas.portfolio.hbase_index.lookup;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Executor;

import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.IndexableFilter;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class InvertedIndexLookupBuilder {
	private final HbaseIndex index;
	private final Executor executor;
	private final LinkedList<IndexableFilter> filters;
	private DataTypeId selectorId;
	private Collection<Indexable> indexables;
	private final Bounds bounds;

	public InvertedIndexLookupBuilder(HbaseIndex index, Executor executor) {
		this.index = index;
		filters = new LinkedList<>();
		bounds = new Bounds(0, 21, MIN_VALUE, MAX_VALUE);
		this.executor = executor;
	}

	public InvertedIndexLookupBuilder setSelectorId(DataTypeId selectorId) {
		this.selectorId = selectorId;
		return this;
	}

	public InvertedIndexLookupBuilder setIndexables(Collection<Indexable> indexables) {
		this.indexables = indexables;
		return this;
	}

	public InvertedIndexLookupBuilder addIndexableFilter(IndexableFilter filter) {
		filters.add(filter);
		return this;
	}

	public void updateBounds(Bounds bounds) {
		this.bounds.update(bounds);
	}

	public InvertedIndexLookup build() {
		Collection<Indexable> relevantIndexables = indexables;
		for (final IndexableFilter filter : filters) {
			relevantIndexables = filter.filter(relevantIndexables);
		}
		return new InvertedIndexLookup(selectorId, relevantIndexables, bounds, index, executor);
	}
}