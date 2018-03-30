package net.sample;

import static net.model.meta_data.RecognitionLevel.UNKNOWN;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Stack;

import net.model.DataType;
import net.model.meta_data.Indexable;
import net.model.meta_data.PreviousKnowledge;
import net.model.meta_data.Reference;
import net.model.meta_data.StatisticsPeriod;
import net.model.types.Document;

public class SampleStorage implements Iterable<DataType> {
	private final Map<String, Map<String, DataType>> storage;
	private Map<String, Map<StatisticsPeriod, Long>> selectorStatistics;
	private Map<String, Map<String, SortedMap<Long, Document>>> invertedIndex;
	private Map<String, List<Reference>> sourceReferences;
	private Map<String, PreviousKnowledge> previousKnowledge;

	public SampleStorage() {
		storage = new HashMap<>();
	}

	public void addDataType(DataType sample) {
		if (!storage.containsKey(sample.getType())) {
			storage.put(sample.getType(), new HashMap<>());
		}
		storage.get(sample.getType()).put(sample.getUid(), sample);
	}

	public DataType getDataType(String type, String uid) {
		if (storage.containsKey(type)) {
			if (storage.get(type).containsKey(uid)) {
				return storage.get(type).get(uid);
			}
		}
		return null;
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
				allTypes.push(entry.getValue().values().iterator());
			}
		}

		@Override
		public boolean hasNext() {
			if (allTypes.isEmpty()) {
				return false;
			}
			while (!allTypes.isEmpty() && !allTypes.peek().hasNext()) {
				allTypes.pop();
			}
			return !allTypes.isEmpty() && allTypes.peek().hasNext();
		}

		@Override
		public DataType next() {
			return allTypes.peek().next();
		}
	}

	public void setReferences(Map<String, List<Reference>> sourceReferences) {
		this.sourceReferences = sourceReferences;

	}

	public List<Reference> getReferences(Document document) {
		if (sourceReferences.containsKey(document.getUid())) {
			return sourceReferences.get(document.getUid());
		}
		return Collections.<Reference> emptyList();
	}

	public List<Document> invertedIndexLookup(DataType selector, Indexable indexable) {
		if (invertedIndex.containsKey(selector.getUid())) {
			if (invertedIndex.get(selector.getUid()).containsKey(indexable.path)) {
				return new LinkedList<>(invertedIndex.get(selector.getUid()).get(indexable.path).values());
			}
		}
		return Collections.<Document> emptyList();
	}

	public Map<StatisticsPeriod, Long> getStatistics(DataType selector) {
		return selectorStatistics.get(selector.getUid());
	}

	public void setPreviousKnowledge(Map<String, PreviousKnowledge> previousKnowledge) {
		this.previousKnowledge = previousKnowledge;
	}

	public PreviousKnowledge lookupPreviousKnowledgeFor(DataType selector) {
		final PreviousKnowledge knowledge = previousKnowledge.get(selector.getUid());
		if (knowledge == null) {
			return new PreviousKnowledge(UNKNOWN, UNKNOWN);
		} else {
			return knowledge;
		}
	}

	public void printSamples(int count) {
		typeLoop: for (final Entry<String, Map<String, DataType>> dataTypeSamples : storage.entrySet()) {
			int i = 0;
			for (final DataType sample : dataTypeSamples.getValue().values()) {
				System.out.println(sample);
				if (i++ >= count) {
					continue typeLoop;
				}
			}
		}
	}

	public Collection<DataType> getAll(String dataType) {
		return new HashSet<>(storage.get(dataType).values());
	}
}
