package net.sample;

import static net.model.meta_data.StatisticsPeriod.DAY;
import static net.model.meta_data.StatisticsPeriod.INFINITY;
import static net.model.meta_data.StatisticsPeriod.QUARTER;
import static net.model.meta_data.StatisticsPeriod.WEEK;
import static net.sample.SampleModel.DATA_TYPE_FIELDS;
import static net.sample.SampleModel.DOCUMENT_TYPES;
import static net.sample.SampleModel.INDEXABLES;
import static net.sample.SampleModel.SELECTOR_TYPES;
import static net.sample.SampleModel.SIMPLE_REPRESENTATION_TYPES;

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

import net.model.DataType;
import net.model.data.Field;
import net.model.data.ReferenceField;
import net.model.meta_data.Indexable;
import net.model.meta_data.PreviousKnowledge;
import net.model.meta_data.Reference;
import net.model.meta_data.StatisticsPeriod;
import net.model.types.Document;
import net.sample.generators.documents.EmailGenerator;
import net.sample.generators.documents.PreviousKnowledgeGenerator;
import net.sample.generators.documents.ReferenceGenerator;
import net.sample.generators.documents.SmsGenerator;
import net.sample.generators.documents.VoiceGenerator;
import net.sample.generators.selectors.DomainGenerator;
import net.sample.generators.selectors.EmailAddressGenerator;
import net.sample.generators.selectors.NameGenerator;
import net.sample.generators.selectors.NumberGenerator;

public class SampleDataSetGenerator {
	private static final long A_DAY = 1000 * 60 * 60 * 24;
	private static final long A_WEEK = 7 * A_DAY;
	private static final long A_MONTH = 30 * A_DAY;

	private static long RANDOM_SEED = 1234l;

	private static SampleStorage storage;
	private static Map<String, DataType> localnames;
	private static Map<String, DataType> displayedNames;
	private static Map<String, DataType> topLevelDomains;
	private static Map<String, DataType> secondLevelDomains;
	private static Map<String, DataType> thirdLevelDomains;
	private static Map<String, DataType> domains;
	private static Map<String, DataType> emailAddresses;
	private static Map<String, DataType> pstnNumbers;
	private static Map<String, DataType> imsiNumbers;
	private static Map<String, DataType> pstnEndpoints;
	private static Map<String, DataType> emailEndpoints;
	private static Map<String, DataType> emails;
	private static Map<String, DataType> smss;
	private static Map<String, DataType> voiceData;

	public static synchronized SampleStorage getSampleDataSet() {
		if (storage == null) {
			storage = new SampleStorage();
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
		return storage;
	}

	private static void generateLocalnames() {
		final Iterable<DataType> generator = new NameGenerator("Localname", "name", 3, 15, 0.0, RANDOM_SEED++);
		localnames = generateSamples(200, generator);
	}

	private static void generateDisplayedNames() {
		final Iterable<DataType> generator = new NameGenerator("DisplayedName", "name", 10, 40, 0.15, RANDOM_SEED++);
		displayedNames = generateSamples(50, generator);
	}

	private static void generateDomains() {
		final Iterable<DataType> generator1 = new DomainGenerator(Collections.emptySet(), 2, 3, false, RANDOM_SEED++);
		topLevelDomains = generateSamples(30, generator1);
		final Iterable<DataType> generator2 = new DomainGenerator(topLevelDomains.values(), 4, 12, false, RANDOM_SEED++);
		secondLevelDomains = generateSamples(200, generator2);
		final Iterable<DataType> generator3 = new DomainGenerator(secondLevelDomains.values(), 4, 12, false, RANDOM_SEED++);
		thirdLevelDomains = generateSamples(50, generator3);
		domains = new HashMap<>();
		domains.putAll(secondLevelDomains);
		domains.putAll(thirdLevelDomains);
	}

	private static void generateEmailAddresses() {
		final Iterable<DataType> generator = new EmailAddressGenerator(localnames.values(), domains.values(), RANDOM_SEED++);
		emailAddresses = generateSamples(200, generator);
	}

	private static void generatePstnNumbers() {
		final Iterable<DataType> generator = new NumberGenerator("Pstn", 6, 14, RANDOM_SEED++);
		pstnNumbers = generateSamples(200, generator);
	}

	private static void generateImsiNumbers() {
		final Iterable<DataType> generator = new NumberGenerator("Imsi", 15, 15, RANDOM_SEED++);
		imsiNumbers = generateSamples(200, generator);
	}

	@SuppressWarnings("unchecked")
	private static void generateEmails() {
		final Iterable<DataType> generator = new EmailGenerator(displayedNames, emailAddresses, RANDOM_SEED++);
		emails = generateSamples(2000, generator);
		for (final DataType sms : emails.values()) {
			final DataType sender = (DataType) sms.get("from");
			emailEndpoints.put(sender.getUid(), sender);
			final List<DataType> toReceivers = (List<DataType>) sms.get("to");
			for (final DataType receiver : toReceivers) {
				emailEndpoints.put(receiver.getUid(), receiver);
			}
			final List<DataType> ccReceivers = (List<DataType>) sms.get("cc");
			for (final DataType receiver : ccReceivers) {
				emailEndpoints.put(receiver.getUid(), receiver);
			}
			final List<DataType> bccReceivers = (List<DataType>) sms.get("bcc");
			for (final DataType receiver : bccReceivers) {
				emailEndpoints.put(receiver.getUid(), receiver);
			}
		}
	}

	private static void generateSmss() {
		final Iterable<DataType> generator = new SmsGenerator(pstnNumbers, imsiNumbers, RANDOM_SEED++);
		smss = generateSamples(2000, generator);
		for (final DataType sms : smss.values()) {
			final DataType sender = (DataType) sms.get("sender");
			pstnEndpoints.put(sender.getUid(), sender);
			final DataType receiver = (DataType) sms.get("receiver");
			pstnEndpoints.put(receiver.getUid(), receiver);
		}
	}

	private static void generateVoice() {
		final Iterable<DataType> generator = new VoiceGenerator(pstnNumbers, imsiNumbers, RANDOM_SEED++);
		voiceData = generateSamples(2000, generator);
		for (final DataType voice : voiceData.values()) {
			final DataType caller = (DataType) voice.get("caller");
			pstnEndpoints.put(caller.getUid(), caller);
			final DataType called = (DataType) voice.get("called");
			pstnEndpoints.put(called.getUid(), called);
		}
	}

	private static Map<String, DataType> generateSamples(final int sampleCount, final Iterable<DataType> generator) {
		final Map<String, DataType> values = new HashMap<>();
		for (final DataType sample : generator) {
			storage.addDataType(sample);
			values.put(sample.getUid(), sample);
			if (values.size() == sampleCount) {
				break;
			}
		}
		return values;
	}

	private static Map<String, Map<String, SortedMap<Long, Document>>> generateInvertedIndex() {
		final InvertedIndexBuilder builder = new InvertedIndexBuilder();
		for (final DataType entity : storage) {
			if (DOCUMENT_TYPES.contains(entity.getType())) {
				final Document document = (Document) entity;
				for (final Indexable indexable : INDEXABLES.get(document.getType())) {
					if (SampleModel.DATA_TYPE_FIELDS.get(indexable.documentType).get(indexable.documentField).isArray()) {
						builder.addSelectorListSubtreeIndex((List<?>) document.get(indexable.documentField), indexable, document);
					} else {
						builder.addSelectorSubtreeIndex((DataType) document.get(indexable.documentField), indexable, document);
					}
				}
			}
		}
		return builder.build();
	}

	private static class InvertedIndexBuilder {
		private final Map<String, Map<String, SortedMap<Long, Document>>> invertedIndex;

		public InvertedIndexBuilder() {
			invertedIndex = new HashMap<>();
		}

		public InvertedIndexBuilder addSelectorListSubtreeIndex(List<?> selectorTree, Indexable indexable, Document document) {
			for (final Object dataType : selectorTree) {
				addSelectorSubtreeIndex((DataType) dataType, indexable, document);
			}
			return this;
		}

		public InvertedIndexBuilder addSelectorSubtreeIndex(DataType selectorTree, Indexable indexable, Document document) {
			final Map<String, DataType> selectors = grabSelectorsFromSubtree(selectorTree);
			for (final DataType selector : selectors.values()) {
				if (!invertedIndex.containsKey(selector.getUid())) {
					invertedIndex.put(selector.getUid(), new HashMap<>());
				}
				if (!invertedIndex.get(selector.getUid()).containsKey(indexable.path)) {
					invertedIndex.get(selector.getUid()).put(indexable.path, new TreeMap<>());
				}
				invertedIndex.get(selector.getUid()).get(indexable.path).put(Long.MAX_VALUE - document.getTimeOfEvent(), document);
			}
			return this;
		}

		public Map<String, Map<String, SortedMap<Long, Document>>> build() {
			return invertedIndex;
		}
	}

	private static Map<String, Map<StatisticsPeriod, Long>> generateSelectorStatistics() {
		final long now = new GregorianCalendar(2017, 10, 17).getTimeInMillis();
		final long yesterday = now - A_DAY;
		final long oneWeekAgo = now - A_WEEK;
		final long threeMonthsAgo = now - 3 * A_MONTH;
		final Map<String, Map<StatisticsPeriod, Long>> allSelectorTotalCounts = new HashMap<>();
		for (final DataType entity : storage) {
			if (DOCUMENT_TYPES.contains(entity.getType())) {
				final Document document = (Document) entity;
				final Map<String, DataType> selectors = grabSelectorsFromSubtree(entity);
				for (final DataType selector : selectors.values()) {
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

	private static Map<StatisticsPeriod, Long> blankSelectorStatistics() {
		final EnumMap<StatisticsPeriod, Long> statistics = new EnumMap<>(StatisticsPeriod.class);
		for (final StatisticsPeriod period : StatisticsPeriod.values()) {
			statistics.put(period, 0l);
		}
		return statistics;
	}

	private static Map<String, DataType> grabSelectorsFromSubtree(DataType entity) {
		if (entity != null) {
			final Map<String, DataType> selectors = new HashMap<>();
			if (SELECTOR_TYPES.contains(entity.getType())) {
				selectors.put(entity.getUid(), entity);
			}
			for (final Field field : DATA_TYPE_FIELDS.get(entity.getType()).values()) {
				if (field instanceof ReferenceField) {
					if (field.isArray()) {
						for (final Object dataType : (List<?>) entity.get(field.getName())) {
							selectors.putAll(grabSelectorsFromSubtree((DataType) dataType));
						}
					} else {
						selectors.putAll(grabSelectorsFromSubtree((DataType) entity.get(field.getName())));
					}
				}
			}
			return selectors;
		} else {
			return Collections.<String, DataType> emptyMap();
		}
	}

	private static Map<String, List<Reference>> generateSourceReferences() {
		final Random random = new Random(RANDOM_SEED++);
		final ReferenceGenerator generator = new ReferenceGenerator(random.nextLong());
		final Map<String, List<Reference>> allReferences = new HashMap<>();
		for (final DataType entity : storage) {
			if (DOCUMENT_TYPES.contains(entity.getType())) {
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

	private static Map<String, PreviousKnowledge> generatePreviousKnowledge() {
		final Random random = new Random(RANDOM_SEED++);
		final PreviousKnowledgeGenerator generator = new PreviousKnowledgeGenerator(random.nextLong());
		final Map<String, PreviousKnowledge> allPreviousKnowledge = new HashMap<>();
		for (final DataType entity : storage) {
			if (SIMPLE_REPRESENTATION_TYPES.contains(entity.getType())) {
				allPreviousKnowledge.put(entity.getUid(), generator.generate());
			}
		}
		return allPreviousKnowledge;
	}

	public static void main(String[] args) {
		getSampleDataSet().printSamples(25);
	}
}
