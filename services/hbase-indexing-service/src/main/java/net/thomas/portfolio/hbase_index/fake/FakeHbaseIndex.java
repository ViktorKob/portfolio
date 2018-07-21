package net.thomas.portfolio.hbase_index.fake;

import static java.lang.Math.random;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;

import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class FakeHbaseIndex implements HbaseIndex, Iterable<DataType> {
	private final Map<String, Map<String, DataType>> storage;
	private Map<String, Map<StatisticsPeriod, Long>> selectorStatistics;
	private Map<String, Map<String, SortedMap<Long, Document>>> invertedIndex;
	private Map<String, Collection<Reference>> sourceReferences;

	public FakeHbaseIndex() {
		storage = new HashMap<>();
	}

	public FakeHbaseIndex(FakeHbaseIndexSerializable serializable) {
		storage = serializable.storage;
		selectorStatistics = serializable.selectorStatistics;
		invertedIndex = serializable.invertedIndex;
		sourceReferences = serializable.sourceReferences;
	}

	public FakeHbaseIndexSerializable getSerializable() {
		return new FakeHbaseIndexSerializable(this);
	}

	public void addEntities(Collection<DataType> entities) {
		for (final DataType sample : entities) {
			addEntity(sample);
		}
	}

	public void addEntity(DataType sample) {
		if (!storage.containsKey(sample.getId().type)) {
			storage.put(sample.getId().type, new HashMap<>());
		}
		storage.get(sample.getId().type)
			.put(sample.getId().uid, sample);
	}

	public void addEntitiesAndChildren(Collection<DataType> entities) {
		for (final DataType sample : entities) {
			addEntityAndChildren(sample);
		}
	}

	public void addEntityAndChildren(DataType entity) {
		addEntity(entity);
		for (final Object field : entity.getFields()
			.values()) {
			if (field instanceof DataType) {
				addEntityAndChildren((DataType) field);
			} else if (field instanceof List) {
				final List<?> values = (List<?>) field;
				for (final Object value : values) {
					if (value instanceof DataType) {
						addEntityAndChildren((DataType) value);
					}
				}
			}
		}
	}

	public void setSelectorStatistics(Map<String, Map<StatisticsPeriod, Long>> selectorStatistics) {
		this.selectorStatistics = selectorStatistics;
	}

	public void setInvertedIndex(Map<String, Map<String, SortedMap<Long, Document>>> invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	@Override
	public Iterator<DataType> iterator() {
		return new StorageIterator();
	}

	private class StorageIterator implements Iterator<DataType> {
		private final Stack<Iterator<DataType>> allTypes;

		public StorageIterator() {
			allTypes = new Stack<>();
			for (final Entry<String, Map<String, DataType>> entry : storage.entrySet()) {
				allTypes.push(entry.getValue()
					.values()
					.iterator());
			}
		}

		@Override
		public boolean hasNext() {
			if (allTypes.isEmpty()) {
				return false;
			}
			while (!allTypes.isEmpty() && !allTypes.peek()
				.hasNext()) {
				allTypes.pop();
			}
			return !allTypes.isEmpty() && allTypes.peek()
				.hasNext();
		}

		@Override
		public DataType next() {
			return allTypes.peek()
				.next();
		}
	}

	public void setReferences(Map<String, Collection<Reference>> sourceReferences) {
		this.sourceReferences = sourceReferences;
	}

	@Override
	public DataType getDataType(DataTypeId id) {
		if (storage.containsKey(id.type)) {
			final Map<String, DataType> typeStorage = storage.get(id.type);
			if (typeStorage.containsKey(id.uid)) {
				return typeStorage.get(id.uid);
			}
		}
		return null;
	}

	@Override
	public DocumentInfos invertedIndexLookup(DataTypeId selectorId, Indexable indexable) {
		final String uid = selectorId.uid;
		if (invertedIndex.containsKey(uid)) {
			return fetchDocumentInfos(indexable, uid);
		} else {
			return new DocumentInfos();
		}
	}

	private DocumentInfos fetchDocumentInfos(Indexable indexable, final String uid) {
		final Map<String, SortedMap<Long, Document>> entityData = invertedIndex.get(uid);
		if (entityData.containsKey(indexable.path)) {
			final Collection<Document> documents = entityData.get(indexable.path)
				.values();
			return new DocumentInfos(documents.stream()
				.map(document -> extractInfo(document))
				.collect(toList()));
		} else {
			return new DocumentInfos();
		}
	}

	private DocumentInfo extractInfo(Document document) {
		return new DocumentInfo(document.getId(), document.getTimeOfEvent(), document.getTimeOfInterception());
	}

	@Override
	public Statistics getStatistics(DataTypeId selectorId) {
		if (selectorStatistics.containsKey(selectorId.uid)) {
			return new Statistics(selectorStatistics.get(selectorId.uid));
		} else {
			return new Statistics();
		}
	}

	@Override
	public References getReferences(DataTypeId documentId) {
		if (sourceReferences.containsKey(documentId.uid)) {
			return new References(sourceReferences.get(documentId.uid));
		} else {
			return new References();
		}
	}

	@Override
	public Entities getSamples(String type, int amount) {
		if (storage.containsKey(type)) {
			if (amount >= storage.get(type)
				.size()) {
				return new Entities(storage.get(type)
					.values());
			} else {
				final List<DataType> instances = new ArrayList<>(storage.get(type)
					.values());
				final Set<DataType> samples = new HashSet<>();
				while (samples.size() < amount) {
					samples.add(getRandomInstance(instances));
				}
				return new Entities(samples);
			}
		} else {
			return new Entities();
		}
	}

	private DataType getRandomInstance(final List<DataType> instances) {
		return instances.get((int) (random() * instances.size()));
	}

	public void printSamples(int amount) {
		for (final String type : storage.keySet()) {
			for (final DataType sample : getSamples(type, amount).getEntities()) {
				System.out.println(sample);
			}
		}
	}

	public Entities getAll(String dataType) {
		return new Entities(storage.get(dataType)
			.values());
	}

	public class FakeHbaseIndexSerializable {
		private Map<String, Map<String, DataType>> storage;
		private Map<String, Map<StatisticsPeriod, Long>> selectorStatistics;
		private Map<String, Map<String, SortedMap<Long, Document>>> invertedIndex;
		private Map<String, Collection<Reference>> sourceReferences;

		public FakeHbaseIndexSerializable() {
		}

		public FakeHbaseIndexSerializable(FakeHbaseIndex index) {
			storage = index.storage;
			selectorStatistics = index.selectorStatistics;
			invertedIndex = index.invertedIndex;
			sourceReferences = index.sourceReferences;
		}

		public Map<String, Map<String, DataType>> getStorage() {
			return storage;
		}

		public void setStorage(Map<String, Map<String, DataType>> storage) {
			this.storage = storage;
		}

		public Map<String, Map<StatisticsPeriod, Long>> getSelectorStatistics() {
			return selectorStatistics;
		}

		public void setSelectorStatistics(Map<String, Map<StatisticsPeriod, Long>> selectorStatistics) {
			this.selectorStatistics = selectorStatistics;
		}

		public Map<String, Map<String, SortedMap<Long, Document>>> getInvertedIndex() {
			return invertedIndex;
		}

		public void setInvertedIndex(Map<String, Map<String, SortedMap<Long, Document>>> invertedIndex) {
			this.invertedIndex = invertedIndex;
		}

		public Map<String, Collection<Reference>> getSourceReferences() {
			return sourceReferences;
		}

		public void setSourceReferences(Map<String, Collection<Reference>> sourceReferences) {
			this.sourceReferences = sourceReferences;
		}
	}
}
