package net.thomas.portfolio.hbase_index.lookup;

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
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class InvertedIndexLookup {
	public static final long MAX_TIMEOUT_IN_SECONDS = 10;

	private final HbaseIndex index;
	private final DataTypeId selectorId;
	private final Collection<Indexable> indexables;
	private final int offset;
	private final int limit;
	private final Executor executor;

	public InvertedIndexLookup(HbaseIndex index, DataTypeId selectorId, Collection<Indexable> indexables, int offset, int limit, Executor executor) {
		this.index = index;
		this.selectorId = selectorId;
		this.indexables = indexables;
		this.offset = offset;
		this.limit = limit;
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
		final Supplier<PriorityQueue<Document>> supplier = createPriorityQueueSupplier();
		final PriorityQueue<Document> allDocuments = resultSets.stream()
			.flatMap(Collection::stream)
			.collect(toCollection(supplier));
		final List<DocumentInfo> documentInfos = new LinkedList<>();
		int count = 0;
		while (count++ < offset && !allDocuments.isEmpty()) {
			allDocuments.poll();
		}
		while (count++ < offset + limit && !allDocuments.isEmpty()) {
			documentInfos.add(extractInfo(allDocuments.poll()));
		}
		return documentInfos;
	}

	private Supplier<PriorityQueue<Document>> createPriorityQueueSupplier() {
		final Comparator<Document> byTimeOfEventInversed = Comparator.comparingLong((document) -> Long.MAX_VALUE - document.getTimeOfEvent());
		final Supplier<PriorityQueue<Document>> supplier = () -> new PriorityQueue<>(byTimeOfEventInversed);
		return supplier;
	}

	private DocumentInfo extractInfo(Document document) {
		return new DocumentInfo(document.getId(), document.getTimeOfEvent(), document.getTimeOfInterception());
	}
}