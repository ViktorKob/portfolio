package net.thomas.portfolio.legal.system;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static net.thomas.portfolio.shared_objects.legal.LegalQueryType.INVERTED_INDEX;
import static net.thomas.portfolio.shared_objects.legal.LegalQueryType.SELECTOR_STATISTICS;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
				throw new AuditLogAccessException("Unable to add audit log for Inverted Index Lookup");
			}
		}

	}

	public synchronized int logStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		synchronized (history) {
			final int nextIndex = history.size();
			if (history.add(selectorStatisticsItemTemplate.itemId(nextIndex).selectorId(selectorId).legalInfo(legalInfo).build())) {
				return nextIndex;
			} else {
				throw new AuditLogAccessException("Unable to add audit log for Statistics Lookup");
			}
		}
	}

	public int getLastId() {
		return history.size() - 1;
	}

	public int getSize() {
		return history.size();
	}

	public List<HistoryItem> get(int page, int size) {
		try {
			final int offset = page * size;
			final int limit = offset + size;
			return unmodifiableList(history.subList(offset, limit > history.size() ? history.size() : limit));
		} catch (final IndexOutOfBoundsException e) {
			return emptyList();
		}
	}

	public Page<HistoryItem> getPage(Pageable pageable) {
		try {
			final int offset = (int) pageable.getOffset();
			final int limit = offset + pageable.getPageSize();
			return new PageImpl<>(history.subList(offset, limit > history.size() ? history.size() : limit), pageable, history.size());
		} catch (final IllegalArgumentException e) {
			return new PageImpl<>(emptyList(), pageable, history.size());
		}
	}

	public HistoryItem getItem(int id) {
		if (id > 0 && id < history.size()) {
			return history.get(id);
		} else {
			return null;
		}
	}

	public static class AuditLogAccessException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public AuditLogAccessException(String message) {
			super(message);
		}

		public AuditLogAccessException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}