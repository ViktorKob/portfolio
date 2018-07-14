package net.thomas.portfolio.hbase_index.fake.index_steps;

import static java.lang.Long.MAX_VALUE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SelectorTraversalTool;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.IndexStep;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.World;

public class FakeInvertedIndexStep implements IndexStep {
	private final SelectorTraversalTool traversalTool;

	public FakeInvertedIndexStep() {
		traversalTool = new SelectorTraversalTool();
	}

	@Override
	public void executeAndUpdateIndex(HbaseIndexSchema schema, World world, HbaseIndex partiallyConstructedIndex) {
		final FakeHbaseIndex index = (FakeHbaseIndex) partiallyConstructedIndex;
		index.setInvertedIndex(generateInvertedIndex(schema, world, partiallyConstructedIndex));
	}

	private Map<String, Map<String, SortedMap<Long, Document>>> generateInvertedIndex(HbaseIndexSchema schema, World world, HbaseIndex index) {
		final InvertedIndexBuilder builder = new InvertedIndexBuilder().setSchema(schema);
		for (final DataType entity : world.getEvents()) {
			final Document document = (Document) entity;
			for (final Indexable indexable : schema.getIndexables(document.getId().type)) {
				final Field field = schema.getFieldForIndexable(indexable);
				if (field.isArray()) {
					builder.addSelectorListSubtreeIndex(document.get(indexable.documentField), indexable, document);
				} else {
					builder.addSelectorSubtreeIndex(document.get(indexable.documentField), indexable, document);
				}
			}
		}
		return builder.build();
	}

	private class InvertedIndexBuilder {
		private final Map<String, Map<String, SortedMap<Long, Document>>> invertedIndex;
		private HbaseIndexSchema schema;

		public InvertedIndexBuilder() {
			invertedIndex = new HashMap<>();
		}

		public InvertedIndexBuilder setSchema(HbaseIndexSchema schema) {
			this.schema = schema;
			return this;
		}

		public InvertedIndexBuilder addSelectorListSubtreeIndex(List<?> selectorTree, Indexable indexable, Document document) {
			for (final Object dataType : selectorTree) {
				addSelectorSubtreeIndex((DataType) dataType, indexable, document);
			}
			return this;
		}

		public InvertedIndexBuilder addSelectorSubtreeIndex(DataType selectorTree, Indexable indexable, Document document) {
			final Map<String, DataType> selectors = traversalTool.grabSelectorsFromSubtree(selectorTree, schema);
			for (final DataType selector : selectors.values()) {
				final String uid = selector.getId().uid;
				if (!invertedIndex.containsKey(uid)) {
					invertedIndex.put(uid, new HashMap<>());
				}
				if (!invertedIndex.get(uid)
					.containsKey(indexable.path)) {
					invertedIndex.get(uid)
						.put(indexable.path, new TreeMap<>());
				}
				invertedIndex.get(uid)
					.get(indexable.path)
					.put(MAX_VALUE - document.getTimeOfEvent(), document);
			}
			return this;
		}

		public Map<String, Map<String, SortedMap<Long, Document>>> build() {
			return invertedIndex;
		}
	}
}