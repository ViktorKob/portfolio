package net.thomas.portfolio.hbase_index.lookup;

import static java.lang.Long.MAX_VALUE;
import static java.util.Collections.synchronizedCollection;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toCollection;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class InvertedIndexLookup {
	public static final long MAX_TIMEOUT_IN_SECONDS = 10;

	private final HbaseIndex index;
	private final DataTypeId selectorId;
	private final Collection<Indexable> indexables;
	private final Bounds bounds;
	private final Executor executor;

	public InvertedIndexLookup(DataTypeId selectorId, Collection<Indexable> indexables, Bounds bounds, HbaseIndex index, Executor executor) {
		this.index = index;
		this.selectorId = selectorId;
		this.indexables = indexables;
		this.bounds = bounds;
		this.executor = executor;
	}

	public DocumentInfos execute() {
		final Collection<DocumentInfos> resultSets = lookupDocuments();
		return extractResult(resultSets);
	}

	private Collection<DocumentInfos> lookupDocuments() {
		final CountDownLatch latch = new CountDownLatch(indexables.size());
		final Collection<DocumentInfos> resultSets = startLookups(latch);
		waitForLatch(latch);
		return resultSets;
	}

	private Collection<DocumentInfos> startLookups(final CountDownLatch latch) {
		final Collection<DocumentInfos> resultSets = synchronizedCollection(new LinkedList<>());
		for (final Indexable indexable : indexables) {
			executor.execute(() -> {
				resultSets.add(index.invertedIndexLookup(selectorId, indexable));
				latch.countDown();
			});
		}
		return resultSets;
	}

	private void waitForLatch(final CountDownLatch latch) {
		try {
			latch.await(MAX_TIMEOUT_IN_SECONDS, SECONDS);
		} catch (final InterruptedException e) {
			// Ignored
		}
	}

	private DocumentInfos extractResult(final Collection<DocumentInfos> resultSets) {
		final Comparator<DocumentInfo> byTimeOfEventInversed = Comparator.comparingLong((info) -> MAX_VALUE - info.getTimeOfEvent());
		final PriorityQueue<DocumentInfo> allDocuments = convertToSortedQueue(resultSets, byTimeOfEventInversed);
		skipUntilDate(allDocuments, bounds.before);
		skipUntilOffset(allDocuments, bounds.offset);
		int count = 0;
		final List<DocumentInfo> result = new LinkedList<>();
		while (!allDocuments.isEmpty() && allDocuments.peek()
			.getTimeOfEvent() >= bounds.after && count++ < bounds.limit) {
			result.add(allDocuments.poll());
		}
		return new DocumentInfos(result);
	}

	private PriorityQueue<DocumentInfo> convertToSortedQueue(final Collection<DocumentInfos> resultSets, final Comparator<DocumentInfo> byTimeOfEventInversed) {
		final Supplier<PriorityQueue<DocumentInfo>> supplier = createPriorityQueueSupplier(byTimeOfEventInversed);
		final PriorityQueue<DocumentInfo> allDocuments = resultSets.stream()
			.map(DocumentInfos::getInfos)
			.flatMap(Collection::stream)
			.collect(toCollection(supplier));
		return allDocuments;
	}

	private void skipUntilDate(final PriorityQueue<DocumentInfo> allDocuments, Long before) {
		while (!allDocuments.isEmpty() && allDocuments.peek()
			.getTimeOfEvent() > before) {
			allDocuments.poll();
		}
	}

	private void skipUntilOffset(final PriorityQueue<DocumentInfo> allDocuments, Integer offset) {
		int count = 0;
		while (!allDocuments.isEmpty() && count++ < offset) {
			allDocuments.poll();
		}
	}

	private Supplier<PriorityQueue<DocumentInfo>> createPriorityQueueSupplier(Comparator<DocumentInfo> byTimeOfEventInversed) {
		return () -> new PriorityQueue<>(byTimeOfEventInversed);
	}
}