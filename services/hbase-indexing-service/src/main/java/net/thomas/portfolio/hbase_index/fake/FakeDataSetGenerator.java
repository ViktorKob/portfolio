package net.thomas.portfolio.hbase_index.fake;

import static java.lang.Long.MAX_VALUE;
import static java.util.Collections.emptySet;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.DAY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.QUARTER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.WEEK;

import java.util.Collection;
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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.hbase_index.fake.generators.documents.EmailGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.ReferenceGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.SmsGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.VoiceGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.people.World;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.DomainGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.EmailAddressGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.NameGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.NumberGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

@Component
@Scope("singleton")
public class FakeDataSetGenerator {
	private static final long A_DAY = 1000 * 60 * 60 * 24;
	private static final long A_WEEK = 7 * A_DAY;
	private static final long A_MONTH = 30 * A_DAY;

	private long randomSeed = 1234l;

	private final FakeHbaseIndexSchemaImpl schema;
	private final FakeHbaseIndex storage;
	private Map<String, DataType> localnames;
	private Map<String, DataType> displayedNames;
	private Map<String, DataType> topLevelDomains;
	private Map<String, DataType> secondLevelDomains;
	private Map<String, DataType> thirdLevelDomains;
	private Map<String, DataType> domains;
	private Map<String, DataType> emailAddresses;
	private Map<String, DataType> pstnNumbers;
	private Map<String, DataType> imsiNumbers;
	private Map<String, DataType> pstnEndpoints;
	private Map<String, DataType> emailEndpoints;
	private Map<String, DataType> emails;
	private Map<String, DataType> smss;
	private Map<String, DataType> voiceData;

	public FakeDataSetGenerator() {
		schema = new FakeHbaseIndexSchemaImpl();
		storage = new FakeHbaseIndex();
	}

	public FakeHbaseIndexSchemaImpl getSchema() {
		return schema;
	}

	public FakeHbaseIndex getSampleDataSet() {
		return storage;
	}

	public void buildSampleDataSet(long randomSeed) {
		this.randomSeed = randomSeed;
		final World world = new World(schema, randomSeed, 100, 10, 1000);
		for (final DataType entity : world.getEntities()) {
			storage.addEntity(entity);
		}

		// generateLocalnames();
		// generateDisplayedNames();
		// generateDomains();
		// generateEmailAddresses();
		// generatePstnNumbers();
		// generateImsiNumbers();
		// emailEndpoints = new HashMap<>();
		// generateEmails();
		// pstnEndpoints = new HashMap<>();
		// generateSmss();
		// generateVoice();
		// indexEndpoints(emailEndpoints);
		// indexEndpoints(pstnEndpoints);
		storage.setInvertedIndex(generateInvertedIndex());
		storage.setSelectorStatistics(generateSelectorStatistics());
		storage.setReferences(generateSourceReferences());
	}

	private void generateLocalnames() {
		final Iterable<DataType> generator = new NameGenerator("Localname", "name", 3, 15, 0.0, schema, randomSeed++);
		localnames = generateSamples(200, generator);
	}

	private void generateDisplayedNames() {
		final Iterable<DataType> generator = new NameGenerator("DisplayedName", "name", 10, 40, 0.15, schema, randomSeed++);
		displayedNames = generateSamples(50, generator);
	}

	private void generateDomains() {
		final Iterable<DataType> generator1 = new DomainGenerator(emptySet(), 2, 3, false, schema, randomSeed++);
		topLevelDomains = generateSamples(30, generator1);
		final Iterable<DataType> generator2 = new DomainGenerator(topLevelDomains.values(), 4, 12, false, schema, randomSeed++);
		secondLevelDomains = generateSamples(200, generator2);
		final Iterable<DataType> generator3 = new DomainGenerator(secondLevelDomains.values(), 4, 12, false, schema, randomSeed++);
		thirdLevelDomains = generateSamples(50, generator3);
		domains = new HashMap<>();
		domains.putAll(secondLevelDomains);
		domains.putAll(thirdLevelDomains);
	}

	private void generateEmailAddresses() {
		final Iterable<DataType> generator = new EmailAddressGenerator(localnames.values(), domains.values(), schema, randomSeed++);
		emailAddresses = generateSamples(200, generator);
	}

	private void generatePstnNumbers() {
		final Iterable<DataType> generator = new NumberGenerator("Pstn", 6, 14, schema, randomSeed++);
		pstnNumbers = generateSamples(200, generator);
	}

	private void generateImsiNumbers() {
		final Iterable<DataType> generator = new NumberGenerator("Imsi", 15, 15, schema, randomSeed++);
		imsiNumbers = generateSamples(200, generator);
	}

	@SuppressWarnings("unchecked")
	private void generateEmails() {
		final Iterable<DataType> generator = new EmailGenerator(displayedNames, emailAddresses, schema, randomSeed++);
		emails = generateSamples(2000, generator);
		for (final DataType sms : emails.values()) {
			final DataType sender = (DataType) sms.get("from");
			emailEndpoints.put(sender.getId().uid, sender);
			final List<DataType> toReceivers = (List<DataType>) sms.get("to");
			for (final DataType receiver : toReceivers) {
				emailEndpoints.put(receiver.getId().uid, receiver);
			}
			final List<DataType> ccReceivers = (List<DataType>) sms.get("cc");
			for (final DataType receiver : ccReceivers) {
				emailEndpoints.put(receiver.getId().uid, receiver);
			}
			final List<DataType> bccReceivers = (List<DataType>) sms.get("bcc");
			for (final DataType receiver : bccReceivers) {
				emailEndpoints.put(receiver.getId().uid, receiver);
			}
		}
	}

	private void generateSmss() {
		final Iterable<DataType> generator = new SmsGenerator(pstnNumbers, imsiNumbers, schema, randomSeed++);
		smss = generateSamples(2000, generator);
		for (final DataType sms : smss.values()) {
			final DataType sender = (DataType) sms.get("sender");
			pstnEndpoints.put(sender.getId().uid, sender);
			final DataType receiver = (DataType) sms.get("receiver");
			pstnEndpoints.put(receiver.getId().uid, receiver);
		}
	}

	private void generateVoice() {
		final Iterable<DataType> generator = new VoiceGenerator(pstnNumbers, imsiNumbers, schema, randomSeed++);
		voiceData = generateSamples(2000, generator);
		for (final DataType voice : voiceData.values()) {
			final DataType caller = (DataType) voice.get("caller");
			pstnEndpoints.put(caller.getId().uid, caller);
			final DataType called = (DataType) voice.get("called");
			pstnEndpoints.put(called.getId().uid, called);
		}
	}

	private void indexEndpoints(Map<String, DataType> endpoints) {
		for (final DataType entity : endpoints.values()) {
			storage.addEntity(entity);
		}
	}

	private Map<String, DataType> generateSamples(final int sampleCount, final Iterable<DataType> generator) {
		final Map<String, DataType> values = new HashMap<>();
		for (final DataType sample : generator) {
			storage.addEntity(sample);
			values.put(sample.getId().uid, sample);
			if (values.size() == sampleCount) {
				break;
			}
		}
		return values;
	}

	private Map<String, Map<String, SortedMap<Long, Document>>> generateInvertedIndex() {
		final InvertedIndexBuilder builder = new InvertedIndexBuilder();
		for (final DataType entity : storage) {
			if (schema.getDocumentTypes()
					.contains(entity.getId().type)) {
				final Document document = (Document) entity;
				for (final Indexable indexable : schema.getIndexables(document.getId().type)) {
					if (schema.getFieldForIndexable(indexable)
							.isArray()) {
						builder.addSelectorListSubtreeIndex((List<?>) document.get(indexable.documentField), indexable, document);
					} else {
						builder.addSelectorSubtreeIndex((DataType) document.get(indexable.documentField), indexable, document);
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
				addSelectorSubtreeIndex((DataType) dataType, indexable, document);
			}
			return this;
		}

		public InvertedIndexBuilder addSelectorSubtreeIndex(DataType selectorTree, Indexable indexable, Document document) {
			final Map<String, DataType> selectors = grabSelectorsFromSubtree(selectorTree);
			for (final DataType selector : selectors.values()) {
				final String uid = selector.getId().uid;
				if (!invertedIndex.containsKey(uid)) {
					invertedIndex.put(uid, new HashMap<>());
				}
				if (!invertedIndex.get(uid)
						.containsKey(indexable.path)) {
					invertedIndex.get(uid)
							.put(indexable.path, new TreeMap<>());
				}
				invertedIndex.get(uid)
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
		for (final DataType entity : storage) {
			if (schema.getDocumentTypes()
					.contains(entity.getId().type)) {
				final Document document = (Document) entity;
				final Map<String, DataType> selectors = grabSelectorsFromSubtree(entity);
				for (final DataType selector : selectors.values()) {
					final String uid = selector.getId().uid;
					if (!allSelectorTotalCounts.containsKey(uid)) {
						allSelectorTotalCounts.put(uid, blankSelectorStatistics());
					}
					final Map<StatisticsPeriod, Long> statistics = allSelectorTotalCounts.get(uid);
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

	private Map<String, DataType> grabSelectorsFromSubtree(DataType entity) {
		if (entity != null) {
			final Map<String, DataType> selectors = new HashMap<>();
			if (schema.getSelectorTypes()
					.contains(entity.getId().type)) {
				selectors.put(entity.getId().uid, entity);
			}
			for (final Field field : schema.getFieldsForDataType(entity.getId().type)) {
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

	private Map<String, Collection<Reference>> generateSourceReferences() {
		final Random random = new Random(randomSeed++);
		final ReferenceGenerator generator = new ReferenceGenerator(random.nextLong());
		final Map<String, Collection<Reference>> allReferences = new HashMap<>();
		for (final DataType entity : storage) {
			if (schema.getDocumentTypes()
					.contains(entity.getId().type)) {
				final Collection<Reference> references = new LinkedList<>();
				final int referenceCount = random.nextInt(3) + 1;
				while (references.size() < referenceCount) {
					references.add(generator.generate());
				}
				allReferences.put(entity.getId().uid, references);
			}
		}
		return allReferences;
	}

	public static void main(String[] args) {
		final FakeDataSetGenerator generator = new FakeDataSetGenerator();
		generator.buildSampleDataSet(1234l);
		generator.getSampleDataSet()
				.printSamples(25);
	}
}
