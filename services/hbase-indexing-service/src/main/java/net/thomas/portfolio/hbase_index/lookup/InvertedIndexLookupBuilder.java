package net.thomas.portfolio.hbase_index.lookup;

import java.util.Collection;
import java.util.concurrent.Executor;

import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class InvertedIndexLookupBuilder {
	private final HbaseIndex index;
	private final Executor executor;
	private DataTypeId selectorId;
	private Collection<Indexable> indexables;
	private int offset;
	private int limit;

	public InvertedIndexLookupBuilder(HbaseIndex index, Executor executor) {
		this.index = index;
		indexables = null;
		offset = 0;
		limit = 21;
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

	public InvertedIndexLookupBuilder setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public InvertedIndexLookupBuilder setLimit(int limit) {
		this.limit = limit;
		return this;
	}

	public InvertedIndexLookup build() {
		if (indexables == null || indexables.isEmpty()) {
			throw new RuntimeException("Indexable(s) must be specified");
		} else if (selectorId == null) {
			throw new RuntimeException("Selector must be specified");
		}
		return new InvertedIndexLookup(index, selectorId, indexables, offset, limit, executor);
	}
}