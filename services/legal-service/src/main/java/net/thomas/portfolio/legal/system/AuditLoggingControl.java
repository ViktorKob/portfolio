package net.thomas.portfolio.legal.system;

import static java.lang.System.currentTimeMillis;
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

	public AuditLoggingControl() {
		history = new ArrayList<>();
	}

	public synchronized boolean logInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return history.add(new HistoryItem(history.size(), INVERTED_INDEX, currentTimeMillis(), selectorId, legalInfo));
	}

	public synchronized boolean logStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return history.add(new HistoryItem(history.size(), SELECTOR_STATISTICS, currentTimeMillis(), selectorId, legalInfo));
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