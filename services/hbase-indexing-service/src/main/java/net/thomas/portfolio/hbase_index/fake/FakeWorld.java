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
import net.thomas.portfolio.hbase_index.fake.generators.selectors.DisplayedNameGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.DomainGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.EmailAddressGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.LocalnameGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.PrivateIdGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.PublicIdGenerator;
import net.thomas.portfolio.hbase_index.fake.world.World;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;

public class FakeWorld extends World {
	private final List<Domain> domains;
	private final List<Person> people;
	private final Map<Integer, List<Person>> relations;

	public FakeWorld(long randomSeed, final int populationCount, final int averageRelationCount, final int averageCommunicationCount) {
		domains = registerDomains(randomSeed);
		people = populateWorld(populationCount, randomSeed++);
		relations = buildRelations(people, averageRelationCount, randomSeed++);
		setEvents(communicate(relations, averageCommunicationCount, randomSeed++));
		setSourceReferences(generateSourceReferences(getEvents(), randomSeed++));
	}

	private List<Domain> registerDomains(long randomSeed) {
		final Random random = new Random(randomSeed++);
		final Iterable<Domain> generator1 = new DomainGenerator(emptySet(), 2, 3, randomSeed++);
		final List<Domain> topLevelDomains = generateSamples(20, 40, random, generator1);
		final Iterable<Domain> generator2 = new DomainGenerator(topLevelDomains, 4, 12, randomSeed++);
		final List<Domain> secondLevelDomains = generateSamples(150, 250, random, generator2);
		final Iterable<Domain> generator3 = new DomainGenerator(secondLevelDomains, 4, 12, randomSeed++);
		final List<Domain> thirdLevelDomains = generateSamples(40, 60, random, generator3);
		final List<Domain> domains = new ArrayList<>();
		domains.addAll(secondLevelDomains);
		domains.addAll(thirdLevelDomains);
		return domains;
	}

	private List<Person> populateWorld(final int populationCount, long randomSeed) {
		final List<Person> people = new ArrayList<>();
		for (int i = 0; i < populationCount; i++) {
			people.add(new Person(randomSeed++));
		}
		return people;
	}

	private Map<Integer, List<Person>> buildRelations(final List<Person> people, final int averageRelationCount, final long randomSeed) {
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
				int tries = 0;
				do {
					nextPersonIndex = random.nextInt(people.size());
					tries++;
				} while (tries < 10 && (nextPersonIndex == personIndex || personalRelations.contains(people.get(nextPersonIndex))));
				personalRelations.add(people.get(nextPersonIndex));
				relations.get(nextPersonIndex).add(thisPerson);
			}
			relations.get(personIndex).addAll(personalRelations);
		}
		return relations;
	}

	private Collection<Event> communicate(final Map<Integer, List<Person>> relations, final int averageCommunicationCount, long randomSeed) {
		final Random random = new Random(randomSeed);
		final Collection<Event> events = new LinkedList<>();
		for (final Entry<Integer, List<Person>> entry : relations.entrySet()) {
			final Person initiator = people.get(entry.getKey());
			final List<Person> personalRelations = entry.getValue();
			final List<Iterator<? extends Event>> eventGenerators = asList(new EmailGenerator(initiator, personalRelations, randomSeed++).iterator(),
					new TextMessageGenerator(initiator, personalRelations, randomSeed++).iterator(),
					new ConversationGenerator(initiator, personalRelations, randomSeed++).iterator());
			final int personalCommunicationCount = averageCommunicationCount / 2 + random.nextInt(averageCommunicationCount);
			for (int eventCount = 0; eventCount < personalCommunicationCount; eventCount++) {
				events.add(randomSample(eventGenerators, random).next());
			}
		}
		return events;
	}

	private Map<String, References> generateSourceReferences(final Collection<Event> events, final long randomSeed) {
		final Random random = new Random(randomSeed);
		final ReferenceGenerator generator = new ReferenceGenerator(random.nextLong());
		final Map<String, References> allReferences = new HashMap<>();
		for (final Event entity : events) {
			final Collection<Reference> references = new LinkedList<>();
			final int referenceCount = random.nextInt(3) + 1;
			while (references.size() < referenceCount) {
				references.add(generator.generate());
			}
			allReferences.put(entity.uid, new References(references));
		}
		return allReferences;
	}

	protected <T> T randomSample(final List<T> values, final Random random) {
		return values.get(random.nextInt(values.size()));
	}

	public class Person {
		public final List<DisplayedName> aliases;
		public final List<Localname> localnames;
		public final List<EmailAddress> emailAddresses;
		public final List<PublicId> publicIdNumbers;
		public final List<PrivateId> privateIdNumbers;

		public Person(long randomSeed) {
			final Random random = new Random(randomSeed++);
			aliases = generateSamples(1, 3, random, new DisplayedNameGenerator(randomSeed++));
			localnames = generateSamples(1, 3, random, new LocalnameGenerator(randomSeed++));
			emailAddresses = generateSamples(1, 3, random, new EmailAddressGenerator(localnames, domains, randomSeed++));
			publicIdNumbers = generateSamples(1, 3, random, new PublicIdGenerator(randomSeed++));
			privateIdNumbers = generateSamples(1, 3, random, new PrivateIdGenerator(randomSeed++));
		}
	}

	private <T> List<T> generateSamples(final int minSampleCount, final int maxSampleCount, final Random random, final Iterable<T> generator) {
		final int sampleCount = minSampleCount + random.nextInt(maxSampleCount - minSampleCount);
		final List<T> values = new ArrayList<>();
		for (final T sample : generator) {
			values.add(sample);
			if (values.size() == sampleCount) {
				break;
			}
		}
		return values;
	}
}