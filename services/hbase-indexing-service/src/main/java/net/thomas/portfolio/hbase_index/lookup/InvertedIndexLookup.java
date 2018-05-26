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
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
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

	public List<DocumentInfo> execute() {
		final Collection<Collection<Document>> resultSets = lookupDocuments();
		final List<DocumentInfo> documents = extractResult(resultSets);
		return documents;
	}

	private Collection<Collection<Document>> lookupDocuments() {
		final CountDownLatch latch = new CountDownLatch(indexables.size());
		final Collection<Collection<Document>> resultSets = startLookups(latch);
		waitForLatch(latch);
		return resultSets;
	}

	private Collection<Collection<Document>> startLookups(final CountDownLatch latch) {
		final Collection<Collection<Document>> resultSets = synchronizedCollection(new LinkedList<>());
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

	private List<DocumentInfo> extractResult(final Collection<Collection<Document>> resultSets) {
		final Comparator<Document> byTimeOfEventInversed = Comparator.comparingLong((document) -> MAX_VALUE - document.getTimeOfEvent());
		final PriorityQueue<Document> allDocuments = convertToSortedQueue(resultSets, byTimeOfEventInversed);
		skipUntilDate(allDocuments, bounds.before);
		skipUntilOffset(allDocuments, bounds.offset);
		int count = 0;
		final List<DocumentInfo> documentInfos = new LinkedList<>();
		while (!allDocuments.isEmpty() && allDocuments.peek()
			.getTimeOfEvent() >= bounds.after && count++ < bounds.limit) {
			documentInfos.add(extractInfo(allDocuments.poll()));
		}
		return documentInfos;
	}

	private PriorityQueue<Document> convertToSortedQueue(final Collection<Collection<Document>> resultSets, final Comparator<Document> byTimeOfEventInversed) {
		final Supplier<PriorityQueue<Document>> supplier = createPriorityQueueSupplier(byTimeOfEventInversed);
		final PriorityQueue<Document> allDocuments = resultSets.stream()
			.flatMap(Collection::stream)
			.collect(toCollection(supplier));
		return allDocuments;
	}

	private void skipUntilDate(final PriorityQueue<Document> allDocuments, Long before) {
		while (!allDocuments.isEmpty() && allDocuments.peek()
			.getTimeOfEvent() > before) {
			allDocuments.poll();
		}
	}

	private void skipUntilOffset(final PriorityQueue<Document> allDocuments, Integer offset) {
		int count = 0;
		while (!allDocuments.isEmpty() && count++ < offset) {
			allDocuments.poll();
		}
	}

	private Supplier<PriorityQueue<Document>> createPriorityQueueSupplier(Comparator<Document> byTimeOfEventInversed) {
		final Supplier<PriorityQueue<Document>> supplier = () -> new PriorityQueue<>(byTimeOfEventInversed);
		return supplier;
	}

	private DocumentInfo extractInfo(Document document) {
		return new DocumentInfo(document.getId(), document.getTimeOfEvent(), document.getTimeOfInterception());
	}
}