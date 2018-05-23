package net.thomas.portfolio.hbase_index.fake;

import static java.lang.Long.MAX_VALUE;
import static java.util.Collections.emptySet;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.DAY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.QUARTER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.WEEK;

import java.util.Collections;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import net.thomas.portfolio.hbase_index.fake.generators.documents.EmailGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.PreviousKnowledgeGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.ReferenceGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.SmsGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.VoiceGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.DomainGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.EmailAddressGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.NameGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.NumberGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class FakeDataSetGenerator {
	private static final long A_DAY = 1000 * 60 * 60 * 24;
	private static final long A_WEEK = 7 * A_DAY;
	private static final long A_MONTH = 30 * A_DAY;

	private long randomSeed = 1234l;

	private final HbaseIndexSchema schema;
	private FakeHbaseIndex storage;
	private Map<String, Datatype> localnames;
	private Map<String, Datatype> displayedNames;
	private Map<String, Datatype> topLevelDomains;
	private Map<String, Datatype> secondLevelDomains;
	private Map<String, Datatype> thirdLevelDomains;
	private Map<String, Datatype> domains;
	private Map<String, Datatype> emailAddresses;
	private Map<String, Datatype> pstnNumbers;
	private Map<String, Datatype> imsiNumbers;
	private Map<String, Datatype> pstnEndpoints;
	private Map<String, Datatype> emailEndpoints;
	private Map<String, Datatype> emails;
	private Map<String, Datatype> smss;
	private Map<String, Datatype> voiceData;

	public FakeDataSetGenerator() {
		schema = new FakeHbaseIndexSchema();
	}

	public HbaseIndexSchema getSchema() {
		return schema;
	}

	public void buildSampleDataSet(long randomSeed) {
		this.randomSeed = randomSeed;
		storage = new FakeHbaseIndex();
		generateLocalnames();
		generateDisplayedNames();
		generateDomains();
		generateEmailAddresses();
		generatePstnNumbers();
		generateImsiNumbers();
		emailEndpoints = new HashMap<>();
		generateEmails();
		pstnEndpoints = new HashMap<>();
		generateSmss();
		generateVoice();
		storage.setInvertedIndex(generateInvertedIndex());
		storage.setSelectorStatistics(generateSelectorStatistics());
		storage.setReferences(generateSourceReferences());
		storage.setPreviousKnowledge(generatePreviousKnowledge());
	}

	private void generateLocalnames() {
		final Iterable<Datatype> generator = new NameGenerator("Localname", "name", 3, 15, 0.0, schema, randomSeed++);
		localnames = generateSamples(200, generator);
	}

	private void generateDisplayedNames() {
		final Iterable<Datatype> generator = new NameGenerator("DisplayedName", "name", 10, 40, 0.15, schema, randomSeed++);
		displayedNames = generateSamples(50, generator);
	}

	private void generateDomains() {
		final Iterable<Datatype> generator1 = new DomainGenerator(emptySet(), 2, 3, false, schema, randomSeed++);
		topLevelDomains = generateSamples(30, generator1);
		final Iterable<Datatype> generator2 = new DomainGenerator(topLevelDomains.values(), 4, 12, false, schema, randomSeed++);
		secondLevelDomains = generateSamples(200, generator2);
		final Iterable<Datatype> generator3 = new DomainGenerator(secondLevelDomains.values(), 4, 12, false, schema, randomSeed++);
		thirdLevelDomains = generateSamples(50, generator3);
		domains = new HashMap<>();
		domains.putAll(secondLevelDomains);
		domains.putAll(thirdLevelDomains);
	}

	private void generateEmailAddresses() {
		final Iterable<Datatype> generator = new EmailAddressGenerator(localnames.values(), domains.values(), schema, randomSeed++);
		emailAddresses = generateSamples(200, generator);
	}

	private void generatePstnNumbers() {
		final Iterable<Datatype> generator = new NumberGenerator("Pstn", 6, 14, schema, randomSeed++);
		pstnNumbers = generateSamples(200, generator);
	}

	private void generateImsiNumbers() {
		final Iterable<Datatype> generator = new NumberGenerator("Imsi", 15, 15, schema, randomSeed++);
		imsiNumbers = generateSamples(200, generator);
	}

	@SuppressWarnings("unchecked")
	private void generateEmails() {
		final Iterable<Datatype> generator = new EmailGenerator(displayedNames, emailAddresses, schema, randomSeed++);
		emails = generateSamples(2000, generator);
		for (final Datatype sms : emails.values()) {
			final Datatype sender = (Datatype) sms.get("from");
			emailEndpoints.put(sender.getUid(), sender);
			final List<Datatype> toReceivers = (List<Datatype>) sms.get("to");
			for (final Datatype receiver : toReceivers) {
				emailEndpoints.put(receiver.getUid(), receiver);
			}
			final List<Datatype> ccReceivers = (List<Datatype>) sms.get("cc");
			for (final Datatype receiver : ccReceivers) {
				emailEndpoints.put(receiver.getUid(), receiver);
			}
			final List<Datatype> bccReceivers = (List<Datatype>) sms.get("bcc");
			for (final Datatype receiver : bccReceivers) {
				emailEndpoints.put(receiver.getUid(), receiver);
			}
		}
	}

	private void generateSmss() {
		final Iterable<Datatype> generator = new SmsGenerator(pstnNumbers, imsiNumbers, schema, randomSeed++);
		smss = generateSamples(2000, generator);
		for (final Datatype sms : smss.values()) {
			final Datatype sender = (Datatype) sms.get("sender");
			pstnEndpoints.put(sender.getUid(), sender);
			final Datatype receiver = (Datatype) sms.get("receiver");
			pstnEndpoints.put(receiver.getUid(), receiver);
		}
	}

	private void generateVoice() {
		final Iterable<Datatype> generator = new VoiceGenerator(pstnNumbers, imsiNumbers, schema, randomSeed++);
		voiceData = generateSamples(2000, generator);
		for (final Datatype voice : voiceData.values()) {
			final Datatype caller = (Datatype) voice.get("caller");
			pstnEndpoints.put(caller.getUid(), caller);
			final Datatype called = (Datatype) voice.get("called");
			pstnEndpoints.put(called.getUid(), called);
		}
	}

	private Map<String, Datatype> generateSamples(final int sampleCount, final Iterable<Datatype> generator) {
		final Map<String, Datatype> values = new HashMap<>();
		for (final Datatype sample : generator) {
			storage.addDataType(sample);
			values.put(sample.getUid(), sample);
			if (values.size() == sampleCount) {
				break;
			}
		}
		return values;
	}

	private Map<String, Map<String, SortedMap<Long, Document>>> generateInvertedIndex() {
		final InvertedIndexBuilder builder = new InvertedIndexBuilder();
		for (final Datatype entity : storage) {
			if (schema.getDocumentTypes()
				.contains(entity.getType())) {
				final Document document = (Document) entity;
				for (final Indexable indexable : schema.getIndexables(document.getType())) {
					if (schema.getFieldForIndexable(indexable)
						.isArray()) {
						builder.addSelectorListSubtreeIndex((List<?>) document.get(indexable.documentField), indexable, document);
					} else {
						builder.addSelectorSubtreeIndex((Datatype) document.get(indexable.documentField), indexable, document);
					}
				}
			}
		}
		return builder.build();
	}

	private class InvertedIndexBuilder {
		private final Map<String, Map<String, SortedMap<Long, Document>>> invertedIndex;

		public InvertedIndexBuilder() {
			invertedIndex = new HashMap<>();
		}

		public InvertedIndexBuilder addSelectorListSubtreeIndex(List<?> selectorTree, Indexable indexable, Document document) {
			for (final Object dataType : selectorTree) {
				addSelectorSubtreeIndex((Datatype) dataType, indexable, document);
			}
			return this;
		}

		public InvertedIndexBuilder addSelectorSubtreeIndex(Datatype selectorTree, Indexable indexable, Document document) {
			final Map<String, Datatype> selectors = grabSelectorsFromSubtree(selectorTree);
			for (final Datatype selector : selectors.values()) {
				if (!invertedIndex.containsKey(selector.getUid())) {
					invertedIndex.put(selector.getUid(), new HashMap<>());
				}
				if (!invertedIndex.get(selector.getUid())
					.containsKey(indexable.path)) {
					invertedIndex.get(selector.getUid())
						.put(indexable.path, new TreeMap<>());
				}
				invertedIndex.get(selector.getUid())
					.get(indexable.path)
					.put(MAX_VALUE - document.getTimeOfEvent(), document);
			}
			return this;
		}

		public Map<String, Map<String, SortedMap<Long, Document>>> build() {
			return invertedIndex;
		}
	}

	private Map<String, Map<StatisticsPeriod, Long>> generateSelectorStatistics() {
		final long now = new GregorianCalendar(2017, 10, 17).getTimeInMillis();
		final long yesterday = now - A_DAY;
		final long oneWeekAgo = now - A_WEEK;
		final long threeMonthsAgo = now - 3 * A_MONTH;
		final Map<String, Map<StatisticsPeriod, Long>> allSelectorTotalCounts = new HashMap<>();
		for (final Datatype entity : storage) {
			if (schema.getDocumentTypes()
				.contains(entity.getType())) {
				final Document document = (Document) entity;
				final Map<String, Datatype> selectors = grabSelectorsFromSubtree(entity);
				for (final Datatype selector : selectors.values()) {
					if (!allSelectorTotalCounts.containsKey(selector.getUid())) {
						allSelectorTotalCounts.put(selector.getUid(), blankSelectorStatistics());
					}
					final Map<StatisticsPeriod, Long> statistics = allSelectorTotalCounts.get(selector.getUid());
					statistics.put(INFINITY, statistics.get(INFINITY) + 1);
					if (document.getTimeOfEvent() > threeMonthsAgo) {
						statistics.put(QUARTER, statistics.get(QUARTER) + 1);
						if (document.getTimeOfEvent() > oneWeekAgo) {
							statistics.put(WEEK, statistics.get(WEEK) + 1);
							if (document.getTimeOfEvent() > yesterday) {
								statistics.put(DAY, statistics.get(DAY) + 1);
							}
						}
					}
				}
			}
		}
		return allSelectorTotalCounts;
	}

	private Map<StatisticsPeriod, Long> blankSelectorStatistics() {
		final Map<StatisticsPeriod, Long> statistics = new EnumMap<>(StatisticsPeriod.class);
		for (final StatisticsPeriod period : StatisticsPeriod.values()) {
			statistics.put(period, 0l);
		}
		return statistics;
	}

	private Map<String, Datatype> grabSelectorsFromSubtree(Datatype entity) {
		if (entity != null) {
			final Map<String, Datatype> selectors = new HashMap<>();
			if (schema.getSelectorTypes()
				.contains(entity.getType())) {
				selectors.put(entity.getUid(), entity);
			}
			for (final Field field : schema.getFieldsForDataType(entity.getType())) {
				if (field instanceof ReferenceField) {
					if (field.isArray()) {
						for (final Object dataType : (List<?>) entity.get(field.getName())) {
							selectors.putAll(grabSelectorsFromSubtree((Datatype) dataType));
						}
					} else {
						selectors.putAll(grabSelectorsFromSubtree((Datatype) entity.get(field.getName())));
					}
				}
			}
			return selectors;
		} else {
			return Collections.<String, Datatype>emptyMap();
		}
	}

	private Map<String, List<Reference>> generateSourceReferences() {
		final Random random = new Random(randomSeed++);
		final ReferenceGenerator generator = new ReferenceGenerator(random.nextLong());
		final Map<String, List<Reference>> allReferences = new HashMap<>();
		for (final Datatype entity : storage) {
			if (schema.getDocumentTypes()
				.contains(entity.getType())) {
				final List<Reference> references = new LinkedList<>();
				final int referenceCount = random.nextInt(3) + 1;
				while (references.size() < referenceCount) {
					references.add(generator.generate());
				}
				allReferences.put(entity.getUid(), references);
			}
		}
		return allReferences;
	}

	private Map<String, PreviousKnowledge> generatePreviousKnowledge() {
		final Random random = new Random(randomSeed++);
		final PreviousKnowledgeGenerator generator = new PreviousKnowledgeGenerator(random.nextLong());
		final Map<String, PreviousKnowledge> allPreviousKnowledge = new HashMap<>();
		for (final Datatype entity : storage) {
			if (schema.getSimpleRepresentableTypes()
				.contains(entity.getType())) {
				allPreviousKnowledge.put(entity.getUid(), generator.generate());
			}
		}
		return allPreviousKnowledge;
	}

	public FakeHbaseIndex getSampleDataSet() {
		return storage;
	}

	public static void main(String[] args) {
		final FakeDataSetGenerator generator = new FakeDataSetGenerator();
		generator.buildSampleDataSet(1234l);
		generator.getSampleDataSet()
			.printSamples(25);
	}
}
