package net.thomas.portfolio.hbase_index.fake.generators.documents;

import static com.google.common.collect.Lists.newLinkedList;
import static java.util.Collections.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.thomas.portfolio.hbase_index.fake.FakeWorld.Person;
import net.thomas.portfolio.hbase_index.fake.generators.DocumentGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.StringGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class EmailGenerator extends DocumentGenerator {
	private final StringGenerator subjectGenerator;
	private final StringGenerator messageGenerator;
	private final IdCalculator uidTool;

	private final Map<String, List<DataType>> previousDisplayedNameMatches;
	private final Person sender;
	private final List<Person> potentialRecipients;

	public EmailGenerator(Person sender, List<Person> potentialRecipients, HbaseIndexSchema schema, long randomSeed) {
		super("Email", schema, randomSeed);
		this.sender = sender;
		this.potentialRecipients = potentialRecipients;
		subjectGenerator = new StringGenerator(0, 125, .2, random.nextLong());
		messageGenerator = new StringGenerator(30, 400, .10, random.nextLong());
		uidTool = new IdCalculator(schema.getFieldsForDataType("EmailEndpoint"), true);
		previousDisplayedNameMatches = new HashMap<>();
	}

	@Override
	protected final boolean keyShouldBeUnique() {
		return false;
	}

	@Override
	protected void populateValues(final DataType sample) {
		sample.put("subject", subjectGenerator.generate());
		sample.put("message", messageGenerator.generate());
		sample.put("from", createEmailEndpoint(sender));
		sample.put("to", createListOfEmailEndpoints(10, 0.3d));
		sample.put("cc", createListOfEmailEndpoints(5, 0.1d));
		sample.put("bcc", createListOfEmailEndpoints(2, 0.05d));
	}

	private List<DataType> createListOfEmailEndpoints(int maxNumberOfElements, double ratioAdjustment) {
		final Set<DataType> addresses = new HashSet<>();
		for (int i = 1; i < maxNumberOfElements; i++) {
			final double roof = 1.0d / i * ratioAdjustment;
			final double value = random.nextDouble();
			if (roof > value) {
				addresses.add(createEmailEndpoint(randomProgressiveSample(potentialRecipients)));
			}
		}
		return newLinkedList(addresses);
	}

	private DataType createEmailEndpoint(Person person) {
		final DataType endpoint = new RawDataType();
		final DataType address = randomProgressiveSample(person.emailAddresses);
		final DataType displayedName = determineDisplayedName(person, address);
		if (displayedName != null) {
			endpoint.put("displayedName", displayedName);
		}
		endpoint.put("address", address);
		endpoint.setId(uidTool.calculate("EmailEndpoint", endpoint));
		return endpoint;
	}

	private DataType determineDisplayedName(Person person, DataType address) {
		final String uid = address.getId().uid;
		if (previousDisplayedNameMatches.containsKey(uid)) {
			if (random.nextDouble() < 0.995) {
				return randomSample(previousDisplayedNameMatches.get(uid));
			}
			final DataType additionalDisplayedName = randomSample(person.aliases);
			previousDisplayedNameMatches.get(uid)
				.add(additionalDisplayedName);
			return additionalDisplayedName;
		} else if (random.nextDouble() < .4) {
			final DataType displayedName = randomSample(person.aliases);
			previousDisplayedNameMatches.put(uid, new ArrayList<>(singleton(displayedName)));
			return displayedName;
		}
		return null;
	}
}
