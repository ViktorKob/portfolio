package net.thomas.portfolio.hbase_index.schema.processing.data;

import static java.lang.Long.MAX_VALUE;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.thomas.portfolio.hbase_index.schema.EntityId;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;

public class InvertedIndex {
	private final Map<String, Map<String, SortedMap<Long, EntityId>>> index;

	public InvertedIndex() {
		index = new HashMap<>();
	}

	public void add(SelectorEntity selector, Event event, String path) {
		final Map<String, SortedMap<Long, EntityId>> uid2Paths2Events = createOrGetEventsOrderByTimeOfEventGroupedByPath(selector);
		final SortedMap<Long, EntityId> paths2Events = createOrGetEventsOrderByTimeOfEvent(path, uid2Paths2Events);
		paths2Events.put(MAX_VALUE - event.timeOfEvent.getTimestamp(), new EntityId(event.getClass(), event.uid));
	}

	private Map<String, SortedMap<Long, EntityId>> createOrGetEventsOrderByTimeOfEventGroupedByPath(SelectorEntity selector) {
		if (!index.containsKey(selector.uid)) {
			index.put(selector.uid, new HashMap<>());
		}
		return index.get(selector.uid);
	}

	private SortedMap<Long, EntityId> createOrGetEventsOrderByTimeOfEvent(String path, final Map<String, SortedMap<Long, EntityId>> uids) {
		if (!uids.containsKey(path)) {
			uids.put(path, new TreeMap<>());
		}
		return uids.get(path);
	}

	public List<EntityId> getEventUids(String uid, String path) {
		if (index.containsKey(uid) && index.get(uid)
			.containsKey(path)) {
			return new ArrayList<>(index.get(uid)
				.get(path)
				.values());
		} else {
			return emptyList();
		}
	}
}