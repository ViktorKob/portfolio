package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface IndexableFilter {
	Collection<Indexable> filter(Collection<Indexable> indexables);

	public static class DataTypeFilter implements IndexableFilter {
		private final Set<String> dataTypes;

		public DataTypeFilter(String... dataTypes) {
			this.dataTypes = new HashSet<>(asList(dataTypes));
		}

		@Override
		public Collection<Indexable> filter(Collection<Indexable> indexables) {
			return indexables.stream()
				.filter((indexable) -> dataTypes.contains(indexable.documentType))
				.collect(toList());
		}
	}

	public static class PathFilter implements IndexableFilter {
		private final Set<String> relations;

		public PathFilter(String... paths) {
			this.relations = new HashSet<>(asList(paths));
		}

		@Override
		public Collection<Indexable> filter(Collection<Indexable> indexables) {
			return indexables.stream()
				.filter((indexable) -> relations.contains(indexable.path))
				.collect(toList());
		}
	}
}
