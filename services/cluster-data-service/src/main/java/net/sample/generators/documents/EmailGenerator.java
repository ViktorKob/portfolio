package net.sample.generators.documents;

import static net.sample.SampleModel.DATA_TYPE_FIELDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.model.DataType;
import net.model.util.UidTool;
import net.sample.generators.DocumentGenerator;
import net.sample.generators.primitives.StringGenerator;

public class EmailGenerator extends DocumentGenerator {
	private final List<DataType> displayedNames;
	private final List<DataType> emailAddresses;
	private final StringGenerator subjectGenerator;
	private final StringGenerator messageGenerator;
	private final UidTool uidTool;

	private final Map<String, List<DataType>> previousDisplayedNameMatches;

	public EmailGenerator(Map<String, DataType> displayedNames, Map<String, DataType> emailAdresses, long randomSeed) {
		super("Email", randomSeed);
		this.displayedNames = new ArrayList<>(displayedNames.values());
		emailAddresses = new ArrayList<>(emailAdresses.values());
		subjectGenerator = new StringGenerator(0, 250, .2, random.nextLong());
		messageGenerator = new StringGenerator(30, 1000, .10, random.nextLong());
		uidTool = new UidTool(DATA_TYPE_FIELDS.get("EmailEndpoint").values(), true);
		previousDisplayedNameMatches = new HashMap<>();
	}

	@Override
	protected final boolean keyShouldBeUnique() {
		return true;
	}

	@Override
	protected void populateValues(final DataType sample) {
		sample.put("subject", subjectGenerator.generate());
		sample.put("message", messageGenerator.generate());
		sample.put("from", createEmailEndpoint(randomSample(emailAddresses)));
		sample.put("to", createListOfEmailEndpoints(1, 30));
		sample.put("cc", createListOfEmailEndpoints(0, 10));
		sample.put("bcc", createListOfEmailEndpoints(0, 2));
	}

	private Object createListOfEmailEndpoints(int minLength, int maxLength) {
		final int targetLength = random.nextInt(maxLength - minLength + 1) + minLength;
		final List<DataType> addresses = new LinkedList<>();
		for (int i = 0; i < targetLength; i++) {
			addresses.add(createEmailEndpoint(randomSample(emailAddresses)));
		}
		return addresses;
	}

	private DataType createEmailEndpoint(DataType address) {
		final DataType endpoint = new DataType("EmailEndpoint");
		final DataType displayedName = determineDisplayedName(address);
		if (displayedName != null) {
			endpoint.put("displayedName", displayedName);
		}
		endpoint.put("address", address);
		endpoint.setUid(uidTool.calculateUid(endpoint));
		return endpoint;
	}

	private DataType determineDisplayedName(DataType address) {
		if (previousDisplayedNameMatches.containsKey(address.getUid())) {
			if (random.nextDouble() < 0.95) {
				return randomSample(previousDisplayedNameMatches.get(address.getUid()));
			}
			final DataType additionalDisplayedName = randomSample(displayedNames);
			previousDisplayedNameMatches.get(address.getUid()).add(additionalDisplayedName);
			return additionalDisplayedName;
		} else if (random.nextDouble() < .4) {
			final DataType displayedName = randomSample(displayedNames);
			previousDisplayedNameMatches.put(address.getUid(), new ArrayList<>(Collections.singleton(displayedName)));
			return displayedName;
		}
		return null;
	}
}
