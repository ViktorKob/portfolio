package net.thomas.portfolio.hbase_index.fake.generators.people;

import static java.util.Collections.emptySet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndexSchemaImpl;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.DomainGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.EmailAddressGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.NameGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.NumberGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public class World {
	private final Set<DataType> entities;
	private final Map<String, DataType> topLevelDomains;
	private final Map<String, DataType> secondLevelDomains;
	private final Map<String, DataType> thirdLevelDomains;
	private final Map<String, DataType> domains;
	private final List<Person> people;
	private final Map<Integer, List<Person>> relations;
	private final List<Document> events;

	public World(FakeHbaseIndexSchemaImpl schema, long randomSeed, int populationCount, int averageRelationCount, int averageCommunicationCount) {
		entities = new HashSet<>();
		final Random random = new Random(randomSeed++);
		final Iterable<DataType> generator1 = new DomainGenerator(emptySet(), 2, 3, false, schema, randomSeed++);
		topLevelDomains = generateSamples(20, 40, random, generator1);
		final Iterable<DataType> generator2 = new DomainGenerator(topLevelDomains.values(), 4, 12, false, schema, randomSeed++);
		secondLevelDomains = generateSamples(150, 250, random, generator2);
		final Iterable<DataType> generator3 = new DomainGenerator(secondLevelDomains.values(), 4, 12, false, schema, randomSeed++);
		thirdLevelDomains = generateSamples(40, 60, random, generator3);
		domains = new HashMap<>();
		domains.putAll(secondLevelDomains);
		domains.putAll(thirdLevelDomains);
		people = populateWorld(populationCount, schema, randomSeed++);
		relations = buildRelations(people, averageRelationCount, schema, randomSeed++);
		events = communicate(relations, averageCommunicationCount, schema, randomSeed++);
	}

	public Set<DataType> getEntities() {
		return entities;
	}

	private List<Person> populateWorld(int populationCount, FakeHbaseIndexSchemaImpl schema, long randomSeed) {
		final List<Person> people = new ArrayList<>();
		for (int i = 0; i < populationCount; i++) {
			people.add(new Person(schema, randomSeed++, this));
		}
		return people;
	}

	private Map<Integer, List<Person>> buildRelations(List<Person> people, int averageRelationCount, FakeHbaseIndexSchemaImpl schema, long randomSeed) {
		final Random random = new Random(randomSeed);
		final Map<Integer, List<Person>> relations = new HashMap<>();
		for (int personIndex = 0; personIndex < people.size(); personIndex++) {
			final int personalRelationsCount = averageRelationCount / 2 + random.nextInt(averageRelationCount);
			final Set<Person> personalRelations = new LinkedHashSet<>();
			for (int relationCount = 0; relationCount < personalRelationsCount; relationCount++) {
				Person nextPerson;
				do {
					nextPerson = people.get(random.nextInt(people.size()));
				} while (nextPerson == people.get(personIndex) || personalRelations.contains(nextPerson));
				personalRelations.add(nextPerson);
			}
			relations.put(personIndex, new ArrayList<>(personalRelations));
		}
		return relations;
	}

	private List<Document> communicate(Map<Integer, List<Person>> relations, int averageCommunicationCount, FakeHbaseIndexSchemaImpl schema, long randomSeed) {
		final LinkedList<Document> events = new LinkedList<>();
		for (final Entry<Integer, List<Person>> entry : relations.entrySet()) {
			final Person rootPerson = people.get(entry.getKey());
			// final Iterable<DataType> emailGenerator = new EmailGenerator(displayedNames, emailAddresses, schema,
			// randomSeed++);
			// final Iterable<DataType> smsGenerator = new SmsGenerator(pstnNumbers, imsiNumbers, schema, randomSeed++);
			// final Iterable<DataType> voiceGenerator = new VoiceGenerator(pstnNumbers, imsiNumbers, schema,
			// randomSeed++);
		}
		return events;
	}

	// private Document generateEmail() {
	// emails = generateSamples(2000, generator);
	// for (final DataType sms : emails.values()) {
	// entities.add((DataType) sms.get("from"));
	// final List<DataType> toReceivers = (List<DataType>) sms.get("to");
	// for (final DataType receiver : toReceivers) {
	// entities.add(receiver);
	// }
	// final List<DataType> ccReceivers = (List<DataType>) sms.get("cc");
	// for (final DataType receiver : ccReceivers) {
	// entities.add(receiver);
	// }
	// final List<DataType> bccReceivers = (List<DataType>) sms.get("bcc");
	// for (final DataType receiver : bccReceivers) {
	// entities.add(receiver);
	// }
	// }
	// }
	//
	// private void generateSms() {
	// smss = generateSamples(2000, generator);
	// for (final DataType sms : smss.values()) {
	// final DataType sender = (DataType) sms.get("sender");
	// pstnEndpoints.put(sender.getId().uid, sender);
	// final DataType receiver = (DataType) sms.get("receiver");
	// pstnEndpoints.put(receiver.getId().uid, receiver);
	// }
	// }
	//
	// private void generateVoice() {
	// voiceData = generateSamples(2000, generator);
	// for (final DataType voice : voiceData.values()) {
	// final DataType caller = (DataType) voice.get("caller");
	// pstnEndpoints.put(caller.getId().uid, caller);
	// final DataType called = (DataType) voice.get("called");
	// pstnEndpoints.put(called.getId().uid, called);
	// }
	// }

	public class Person {
		public final Map<String, DataType> localnames;
		public final Map<String, DataType> aliases;
		public final Map<String, DataType> emailAddresses;
		public final Map<String, DataType> pstnNumbers;
		public final Map<String, DataType> imsiNumbers;
		public final Map<String, DataType> imeiNumbers;

		public Person(FakeHbaseIndexSchemaImpl schema, long randomSeed, World world) {
			final Random random = new Random(randomSeed++);
			aliases = generateSamples(1, 3, random, new NameGenerator("DisplayedName", "name", 3, 15, 0.15, schema, randomSeed++));
			localnames = generateSamples(1, 3, random, new NameGenerator("Localname", "name", 3, 15, 0.0, schema, randomSeed++));
			emailAddresses = generateSamples(1, 3, random, new EmailAddressGenerator(localnames.values(), domains.values(), schema, randomSeed++));
			pstnNumbers = generateSamples(1, 3, random, new NumberGenerator("Pstn", 6, 14, schema, randomSeed++));
			imsiNumbers = generateSamples(1, 3, random, new NumberGenerator("Imsi", 15, 15, schema, randomSeed++));
			imeiNumbers = generateSamples(1, 3, random, new NumberGenerator("Imsi", 15, 15, schema, randomSeed++));
		}
	}

	private Map<String, DataType> generateSamples(final int minSampleCount, final int maxSampleCount, Random random, final Iterable<DataType> generator) {
		final int sampleCount = minSampleCount + random.nextInt(maxSampleCount - minSampleCount);
		final Map<String, DataType> values = new HashMap<>();
		for (final DataType sample : generator) {
			entities.add(sample);
			values.put(sample.getId().uid, sample);
			if (values.size() == sampleCount) {
				break;
			}
		}
		return values;
	}
}