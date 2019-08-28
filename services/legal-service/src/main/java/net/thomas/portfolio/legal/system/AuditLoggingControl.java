package net.thomas.portfolio.legal.system;

import static java.util.Collections.unmodifiableList;
import static net.thomas.portfolio.shared_objects.legal.LegalQueryType.INVERTED_INDEX;
import static net.thomas.portfolio.shared_objects.legal.LegalQueryType.SELECTOR_STATISTICS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.HistoryItem;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@Component
@Scope("singleton")
public class AuditLoggingControl {
	private final List<HistoryItem> history;
	private final HistoryItem.HistoryItemBuilder invertedIndexItemTemplate;
	private final HistoryItem.HistoryItemBuilder selectorStatisticsItemTemplate;

	public AuditLoggingControl() {
		history = new ArrayList<>();
		new HistoryItem();
		invertedIndexItemTemplate = HistoryItem.builder().type(INVERTED_INDEX);
		selectorStatisticsItemTemplate = HistoryItem.builder().type(SELECTOR_STATISTICS);
	}

	public synchronized boolean logInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		synchronized (history) {
			final int nextIndex = history.size();
			return history.add(invertedIndexItemTemplate.itemId(nextIndex).selectorId(selectorId).legalInfo(legalInfo).build());
		}
	}

	public synchronized boolean logStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		synchronized (history) {
			final int nextIndex = history.size();
			return history.add(selectorStatisticsItemTemplate.itemId(nextIndex).selectorId(selectorId).legalInfo(legalInfo).build());
		}
	}

	public int getLastId() {
		return history.size() - 1;
	}

	public List<HistoryItem> getAll() {
		return unmodifiableList(history);
	}

	public HistoryItem getItem(int id) {
		if (id > 0 && id < history.size()) {
			return history.get(id);
		} else {
			return null;
		}
	}
}