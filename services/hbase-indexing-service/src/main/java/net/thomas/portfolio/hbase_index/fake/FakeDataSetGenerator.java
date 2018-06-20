package net.thomas.portfolio.hbase_index.fake;

import static java.lang.Long.MAX_VALUE;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.DAY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.QUARTER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.WEEK;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.hbase_index.fake.generators.documents.ReferenceGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

@Component
@Scope("singleton")
public class FakeDataSetGenerator {
	private static final long A_DAY = 1000 * 60 * 60 * 24;
	private static final long A_WEEK = 7 * A_DAY;
	private static final long A_MONTH = 30 * A_DAY;

	private long randomSeed = 1234l;

	private final FakeHbaseIndexSchemaImpl schema;
	private final FakeHbaseIndex storage;

	public FakeDataSetGenerator() {
		schema = new FakeHbaseIndexSchemaImpl();
		storage = new FakeHbaseIndex();
	}

	public FakeHbaseIndexSchemaImpl getSchema() {
		return schema;
	}

	public FakeHbaseIndex getSampleDataSet() {
		return storage;
	}

	public void buildSampleDataSet(long randomSeed) {
		this.randomSeed = randomSeed;
		final FakeWorldInitializer world = new FakeWorldInitializer(schema, randomSeed, 100, 10, 1000);
		for (final DataType entity : world.getEvents()) {
			addAllSubEntitiesToStorage(entity);
		}

		storage.setInvertedIndex(generateInvertedIndex());
		storage.setSelectorStatistics(generateSelectorStatistics());
		storage.setReferences(generateSourceReferences());
	}

	private void addAllSubEntitiesToStorage(DataType entity) {
		storage.addEntity(entity);
		for (final Object field : entity.getFields()
			.values()) {
			if (field instanceof DataType) {
				addAllSubEntitiesToStorage((DataType) field);
			}
		}
	}

	private Map<String, Map<String, SortedMap<Long, Document>>> generateInvertedIndex() {
		final InvertedIndexBuilder builder = new InvertedIndexBuilder();
		for (final DataType entity : storage) {
			if (schema.getDocumentTypes()
				.contains(entity.getId().type)) {
				final Document document = (Document) entity;
				for (final Indexable indexable : schema.getIndexables(document.getId().type)) {
					if (schema.getFieldForIndexable(indexable)
						.isArray()) {
						builder.addSelectorListSubtreeIndex((List<?>) document.get(indexable.documentField), indexable, document);
					} else {
						builder.addSelectorSubtreeIndex((DataType) document.get(indexable.documentField), indexable, document);
					}
				}
			}
		}
		return builder.build();
	}

	private class InvertedIndexBuilder {
		private final Map<String, Map<String, SortedMap<Long, Document>>> invertedIndex;

		public InvertedIndexBuilder() {
			invertedIndex = new HashMap<>();
		}

		public InvertedIndexBuilder addSelectorListSubtreeIndex(List<?> selectorTree, Indexable indexable, Document document) {
			for (final Object dataType : selectorTree) {
				addSelectorSubtreeIndex((DataType) dataType, indexable, document);
			}
			return this;
		}

		public InvertedIndexBuilder addSelectorSubtreeIndex(DataType selectorTree, Indexable indexable, Document document) {
			final Map<String, DataType> selectors = grabSelectorsFromSubtree(selectorTree);
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

	private Map<String, Map<StatisticsPeriod, Long>> generateSelectorStatistics() {
		final long now = new GregorianCalendar(2017, 10, 17).getTimeInMillis();
		final long yesterday = now - A_DAY;
		final long oneWeekAgo = now - A_WEEK;
		final long threeMonthsAgo = now - 3 * A_MONTH;
		final Map<String, Map<StatisticsPeriod, Long>> allSelectorTotalCounts = new HashMap<>();
		for (final DataType entity : storage) {
			if (schema.getDocumentTypes()
				.contains(entity.getId().type)) {
				final Document document = (Document) entity;
				final Map<String, DataType> selectors = grabSelectorsFromSubtree(entity);
				for (final DataType selector : selectors.values()) {
					final String uid = selector.getId().uid;
					if (!allSelectorTotalCounts.containsKey(uid)) {
						allSelectorTotalCounts.put(uid, blankSelectorStatistics());
					}
					final Map<StatisticsPeriod, Long> statistics = allSelectorTotalCounts.get(uid);
					statistics.put(INFINITY, statistics.get(INFINITY) + 1);
					if (document.getTimeOfEvent() > threeMonthsAgo) {
						statistics.put(QUARTER, statistics.get(QUARTER) + 1);
						if (document.getTimeOfEvent() > oneWeekAgo) {
							statistics.put(WEEK, statistics.get(WEEK) + 1);
							if (document.getTimeOfEvent() > yesterday) {
								statistics.put(DAY, statistics.get(DAY) + 1);
							}
						}
					}
				}
			}
		}
		return allSelectorTotalCounts;
	}

	private Map<StatisticsPeriod, Long> blankSelectorStatistics() {
		final Map<StatisticsPeriod, Long> statistics = new EnumMap<>(StatisticsPeriod.class);
		for (final StatisticsPeriod period : StatisticsPeriod.values()) {
			statistics.put(period, 0l);
		}
		return statistics;
	}

	private Map<String, DataType> grabSelectorsFromSubtree(DataType entity) {
		if (entity != null) {
			final Map<String, DataType> selectors = new HashMap<>();
			if (schema.getSelectorTypes()
				.contains(entity.getId().type)) {
				selectors.put(entity.getId().uid, entity);
			}
			for (final Field field : schema.getFieldsForDataType(entity.getId().type)) {
				if (field instanceof ReferenceField) {
					if (field.isArray()) {
						for (final Object dataType : (List<?>) entity.get(field.getName())) {
							selectors.putAll(grabSelectorsFromSubtree((DataType) dataType));
						}
					} else {
						selectors.putAll(grabSelectorsFromSubtree((DataType) entity.get(field.getName())));
					}
				}
			}
			return selectors;
		} else {
			return Collections.<String, DataType>emptyMap();
		}
	}

	private Map<String, Collection<Reference>> generateSourceReferences() {
		final Random random = new Random(randomSeed++);
		final ReferenceGenerator generator = new ReferenceGenerator(random.nextLong());
		final Map<String, Collection<Reference>> allReferences = new HashMap<>();
		for (final DataType entity : storage) {
			if (schema.getDocumentTypes()
				.contains(entity.getId().type)) {
				final Collection<Reference> references = new LinkedList<>();
				final int referenceCount = random.nextInt(3) + 1;
				while (references.size() < referenceCount) {
					references.add(generator.generate());
				}
				allReferences.put(entity.getId().uid, references);
			}
		}
		return allReferences;
	}

	public static void main(String[] args) {
		final FakeDataSetGenerator generator = new FakeDataSetGenerator();
		generator.buildSampleDataSet(1234l);
		generator.getSampleDataSet()
			.printSamples(25);
	}
}
