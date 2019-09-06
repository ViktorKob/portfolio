package net.thomas.portfolio.legal.system;

import static java.util.Collections.unmodifiableList;
import static net.thomas.portfolio.shared_objects.legal.LegalQueryType.INVERTED_INDEX;
import static net.thomas.portfolio.shared_objects.legal.LegalQueryType.SELECTOR_STATISTICS;

import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.HistoryItem;
import net.thomas.portfolio.shared_objects.legal.HistoryItemList;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@Component
@Scope("singleton")
public class AuditLoggingControl {
	private final HistoryItemList history;
	private final HistoryItem.HistoryItemBuilder invertedIndexItemTemplate;
	private final HistoryItem.HistoryItemBuilder selectorStatisticsItemTemplate;

	public AuditLoggingControl() {
		history = new HistoryItemList();
		invertedIndexItemTemplate = HistoryItem.builder().type(INVERTED_INDEX);
		selectorStatisticsItemTemplate = HistoryItem.builder().type(SELECTOR_STATISTICS);
	}

	public synchronized int logInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		synchronized (history) {
			final int nextIndex = history.size();
			if (history.add(invertedIndexItemTemplate.itemId(nextIndex).selectorId(selectorId).legalInfo(legalInfo).build())) {
				return nextIndex;
			} else {
				throw new RuntimeException("Unable to add audit log");
			}
		}

	}

	public synchronized int logStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		synchronized (history) {
			final int nextIndex = history.size();
			if (history.add(selectorStatisticsItemTemplate.itemId(nextIndex).selectorId(selectorId).legalInfo(legalInfo).build())) {
				return nextIndex;
			} else {
				throw new RuntimeException("Unable to add audit log");
			}
		}
	}

	public int getLastId() {
		return history.size() - 1;
	}

	public List<HistoryItem> getAll(Pageable pageable) {
		// TODO[Thomas]: Figure out how to do this in practice
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