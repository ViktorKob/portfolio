package net.thomas.portfolio.hbase_index.fake;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.thomas.portfolio.hbase_index.fake.generators.documents.ConversationGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.EmailGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.ReferenceGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.documents.TextMessageGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.DomainGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.EmailAddressGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.NameGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.NumberGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.World;

public class FakeWorld extends World {
	private final List<DataType> domains;
	private final List<Person> people;
	private final Map<Integer, List<Person>> relations;

	public FakeWorld(HbaseIndexSchema schema, long randomSeed, int populationCount, int averageRelationCount, int averageCommunicationCount) {
		domains = registerDomains(schema, randomSeed);
		people = populateWorld(populationCount, schema, randomSeed++);
		relations = buildRelations(people, averageRelationCount, schema, randomSeed++);
		setEvents(communicate(relations, averageCommunicationCount, schema, randomSeed++));
		setSourceReferences(generateSourceReferences(getEvents(), randomSeed++));
	}

	private List<DataType> registerDomains(HbaseIndexSchema schema, long randomSeed) {
		final Random random = new Random(randomSeed++);
		final Iterable<DataType> generator1 = new DomainGenerator(emptySet(), 2, 3, false, schema, randomSeed++);
		final List<DataType> topLevelDomains = generateSamples(20, 40, random, generator1);
		final Iterable<DataType> generator2 = new DomainGenerator(topLevelDomains, 4, 12, false, schema, randomSeed++);
		final List<DataType> secondLevelDomains = generateSamples(150, 250, random, generator2);
		final Iterable<DataType> generator3 = new DomainGenerator(secondLevelDomains, 4, 12, false, schema, randomSeed++);
		final List<DataType> thirdLevelDomains = generateSamples(40, 60, random, generator3);
		final List<DataType> domains = new ArrayList<>();
		domains.addAll(secondLevelDomains);
		domains.addAll(thirdLevelDomains);
		return domains;
	}

	private List<Person> populateWorld(int populationCount, HbaseIndexSchema schema, long randomSeed) {
		final List<Person> people = new ArrayList<>();
		for (int i = 0; i < populationCount; i++) {
			people.add(new Person(schema, randomSeed++, this));
		}
		return people;
	}

	private Map<Integer, List<Person>> buildRelations(List<Person> people, int averageRelationCount, HbaseIndexSchema schema, long randomSeed) {
		final Random random = new Random(randomSeed);
		final Map<Integer, List<Person>> relations = new HashMap<>();
		for (int personIndex = 0; personIndex < people.size(); personIndex++) {
			relations.put(personIndex, new ArrayList<>());
		}
		for (int personIndex = 0; personIndex < people.size(); personIndex++) {
			final Person thisPerson = people.get(personIndex);
			final int personalRelationsCount = averageRelationCount / 2 + random.nextInt(averageRelationCount);
			final Set<Person> personalRelations = new LinkedHashSet<>(relations.get(personIndex));
			for (int relationCount = 0; relationCount < personalRelationsCount; relationCount++) {
				int nextPersonIndex;
				do {
					nextPersonIndex = random.nextInt(people.size());
				} while (nextPersonIndex == personIndex || personalRelations.contains(people.get(nextPersonIndex)));
				personalRelations.add(people.get(nextPersonIndex));
				relations.get(nextPersonIndex)
					.add(thisPerson);
			}
			relations.get(personIndex)
				.addAll(personalRelations);
		}
		return relations;
	}

	private Collection<DataType> communicate(Map<Integer, List<Person>> relations, int averageCommunicationCount, HbaseIndexSchema schema, long randomSeed) {
		final Random random = new Random(randomSeed);
		final Collection<DataType> events = new LinkedList<>();
		for (final Entry<Integer, List<Person>> entry : relations.entrySet()) {
			final Person initiator = people.get(entry.getKey());
			final List<Person> personalRelations = entry.getValue();
			final List<Iterator<DataType>> eventGenerators = asList(new EmailGenerator(initiator, personalRelations, schema, randomSeed++).iterator(),
					new TextMessageGenerator(initiator, personalRelations, schema, randomSeed++).iterator(),
					new ConversationGenerator(initiator, personalRelations, schema, randomSeed++).iterator());
			final int personalCommunicationCount = averageCommunicationCount / 2 + random.nextInt(averageCommunicationCount);
			for (int eventCount = 0; eventCount < personalCommunicationCount; eventCount++) {
				events.add(randomSample(eventGenerators, random).next());
			}
		}
		return events;
	}

	private Map<String, Collection<Reference>> generateSourceReferences(Collection<DataType> events, long randomSeed) {
		final Random random = new Random(randomSeed);
		final ReferenceGenerator generator = new ReferenceGenerator(random.nextLong());
		final Map<String, Collection<Reference>> allReferences = new HashMap<>();
		for (final DataType entity : events) {
			final Collection<Reference> references = new LinkedList<>();
			final int referenceCount = random.nextInt(3) + 1;
			while (references.size() < referenceCount) {
				references.add(generator.generate());
			}
			allReferences.put(entity.getId().uid, references);
		}
		return allReferences;
	}

	protected <T> T randomSample(List<T> values, Random random) {
		return values.get(random.nextInt(values.size()));
	}

	public class Person {
		public final List<DataType> aliases;
		public final List<DataType> localnames;
		public final List<DataType> emailAddresses;
		public final List<DataType> publicIdNumbers;
		public final List<DataType> privateIdNumbers;

		public Person(HbaseIndexSchema schema, long randomSeed, FakeWorld world) {
			final Random random = new Random(randomSeed++);
			aliases = generateSamples(1, 3, random, new NameGenerator("DisplayedName", "name", 3, 15, 0.15, schema, randomSeed++));
			localnames = generateSamples(1, 3, random, new NameGenerator("Localname", "name", 3, 15, 0.0, schema, randomSeed++));
			emailAddresses = generateSamples(1, 3, random, new EmailAddressGenerator(localnames, domains, schema, randomSeed++));
			publicIdNumbers = generateSamples(1, 3, random, new NumberGenerator("PublicId", 6, 14, schema, randomSeed++));
			privateIdNumbers = generateSamples(1, 3, random, new NumberGenerator("PrivateId", 15, 15, schema, randomSeed++));
		}
	}

	private List<DataType> generateSamples(final int minSampleCount, final int maxSampleCount, Random random, final Iterable<DataType> generator) {
		final int sampleCount = minSampleCount + random.nextInt(maxSampleCount - minSampleCount);
		final List<DataType> values = new ArrayList<>();
		for (final DataType sample : generator) {
			values.add(sample);
			if (values.size() == sampleCount) {
				break;
			}
		}
		return values;
	}
}