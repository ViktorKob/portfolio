package net.sample.generators.documents;

import static net.sample.SampleModel.DATA_TYPE_FIELDS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.model.DataType;
import net.model.types.GeoLocation;
import net.model.util.UidTool;
import net.sample.generators.DocumentGenerator;
import net.sample.generators.primitives.StringGenerator;

public class SmsGenerator extends DocumentGenerator {
	private final List<DataType> pstnNumbers;
	private final List<DataType> imsiNumbers;
	private final StringGenerator messageGenerator;
	private final UidTool uidTool;

	public SmsGenerator(Map<String, DataType> pstnNumbers, Map<String, DataType> imsiNumbers, long randomSeed) {
		super("Sms", randomSeed);
		this.pstnNumbers = new ArrayList<>(pstnNumbers.values());
		this.imsiNumbers = new ArrayList<>(imsiNumbers.values());
		messageGenerator = new StringGenerator(5, 250, 0.1, random.nextLong());
		uidTool = new UidTool(DATA_TYPE_FIELDS.get("PstnEndpoint").values(), true);
	}

	@Override
	protected final boolean keyShouldBeUnique() {
		return true;
	}

	@Override
	protected void populateValues(final DataType sample) {
		sample.put("message", messageGenerator.generate());
		sample.put("sender", createPstnEndpoint("pstn", randomSample(pstnNumbers)));
		sample.put("receiver", createPstnEndpoint("imsi", randomSample(imsiNumbers)));

		if (random.nextDouble() < 0.1) {
			sample.put("senderLocation", new GeoLocation(random.nextDouble() * 360 - 180, random.nextDouble() * 180 - 90));
		}
		if (random.nextDouble() < 0.1) {
			sample.put("receiverLocation", new GeoLocation(random.nextDouble() * 360 - 180, random.nextDouble() * 180 - 90));
		}
	}

	private DataType createPstnEndpoint(String numberField, DataType number) {
		final DataType endpoint = new DataType("PstnEndpoint");
		endpoint.put(numberField, number);
		endpoint.setUid(uidTool.calculateUid(endpoint));
		return endpoint;
	}
}
