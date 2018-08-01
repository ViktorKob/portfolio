package net.thomas.portfolio.hbase_index.fake.generators.documents;

import static java.util.Collections.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.thomas.portfolio.hbase_index.fake.FakeWorld.Person;
import net.thomas.portfolio.hbase_index.fake.generators.EventGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.StringGenerator;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class EmailGenerator extends EventGenerator<Email> {
	private final Map<String, List<DisplayedName>> previousDisplayedNameMatches;
	private final Person sender;
	private final List<Person> potentialRecipients;
	private final StringGenerator subjectGenerator;
	private final StringGenerator messageGenerator;

	public EmailGenerator(Person sender, List<Person> potentialRecipients, long randomSeed) {
		super(randomSeed);
		this.sender = sender;
		this.potentialRecipients = potentialRecipients;
		subjectGenerator = new StringGenerator(0, 125, .2, random.nextLong());
		messageGenerator = new StringGenerator(30, 400, .10, random.nextLong());
		previousDisplayedNameMatches = new HashMap<>();
	}

	@Override
	protected Email createInstance() {
		final Timestamp timeOfEvent = generateTimeOfEvent();
		final Timestamp timeOfInterception = generateTimeOfInterception(timeOfEvent);
		final String subject = subjectGenerator.generate();
		final String message = messageGenerator.generate();

		final EmailEndpoint from = createEmailEndpoint(sender);
		final EmailEndpoint[] to = createListOfEmailEndpoints(10, 0.3d);
		final EmailEndpoint[] cc = createListOfEmailEndpoints(5, 0.1d);
		final EmailEndpoint[] bcc = createListOfEmailEndpoints(2, 0.05d);
		return new Email(subject, message, from, to, cc, bcc, timeOfEvent, timeOfInterception);
	}

	private EmailEndpoint createEmailEndpoint(Person person) {
		final EmailAddress address = randomProgressiveSample(person.emailAddresses);
		final DisplayedName displayedName = determineDisplayedName(person, address);
		final EmailEndpoint endpoint = new EmailEndpoint(displayedName, address);
		endpoint.uid = idTool.calculate(endpoint);
		return endpoint;
	}

	private DisplayedName determineDisplayedName(Person person, EmailAddress address) {
		if (previousDisplayedNameMatches.containsKey(address.uid)) {
			if (random.nextDouble() < 0.995) {
				return randomSample(previousDisplayedNameMatches.get(address.uid));
			}
			final DisplayedName additionalDisplayedName = randomSample(person.aliases);
			previousDisplayedNameMatches.get(address.uid)
				.add(additionalDisplayedName);
			return additionalDisplayedName;
		} else if (random.nextDouble() < .4) {
			final DisplayedName displayedName = randomSample(person.aliases);
			previousDisplayedNameMatches.put(address.uid, new ArrayList<>(singleton(displayedName)));
			return displayedName;
		}
		return null;
	}

	private EmailEndpoint[] createListOfEmailEndpoints(int maxNumberOfElements, double ratioAdjustment) {
		final Set<EmailEndpoint> endpoints = new HashSet<>();
		for (int i = 1; i < maxNumberOfElements; i++) {
			final double roof = 1.0d / i * ratioAdjustment;
			final double value = random.nextDouble();
			if (roof > value) {
				endpoints.add(createEmailEndpoint(randomProgressiveSample(potentialRecipients)));
			}
		}
		return endpoints.toArray(new EmailEndpoint[endpoints.size()]);
	}
}