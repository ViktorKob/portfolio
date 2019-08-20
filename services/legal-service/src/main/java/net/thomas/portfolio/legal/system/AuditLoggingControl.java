package net.thomas.portfolio.legal.system;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.unmodifiableList;
import static net.thomas.portfolio.legal.system.AuditLoggingControl.QueryType.INVERTED_INDEX;
import static net.thomas.portfolio.legal.system.AuditLoggingControl.QueryType.SELECTOR_STATISTICS;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@Component
@Scope("singleton")
public class AuditLoggingControl {
	private final LinkedList<HistoryItem> history;

	public AuditLoggingControl() {
		history = new LinkedList<>();
	}

	public boolean logInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return history.add(new HistoryItem(INVERTED_INDEX, currentTimeMillis(), selectorId, legalInfo));
	}

	public boolean logStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		return history.add(new HistoryItem(SELECTOR_STATISTICS, currentTimeMillis(), selectorId, legalInfo));
	}

	public List<HistoryItem> getHistory() {
		return unmodifiableList(history);
	}

	public static enum QueryType {
		INVERTED_INDEX,
		SELECTOR_STATISTICS
	}

	@RequiredArgsConstructor
	@ToString
	public static class HistoryItem {
		public final QueryType type;
		public final long timeOfLogging;
		public final DataTypeId selectorId;
		public final LegalInformation legalInfo;
	}
}