package net.sample.generators.documents;

import static net.sample.SampleModel.DATA_TYPE_FIELDS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.model.DataType;
import net.model.types.GeoLocation;
import net.model.util.UidGenerator;
import net.sample.generators.DocumentGenerator;

public class VoiceGenerator extends DocumentGenerator {
	private final List<DataType> pstnNumbers;
	private final UidGenerator uidTool;

	public VoiceGenerator(Map<String, DataType> pstnNumbers, Map<String, DataType> imsiNumbers, long randomSeed) {
		super("Voice", randomSeed);
		this.pstnNumbers = new ArrayList<>(pstnNumbers.values());
		uidTool = new UidGenerator(DATA_TYPE_FIELDS.get("PstnEndpoint")
			.values(), true);
	}

	@Override
	protected final boolean keyShouldBeUnique() {
		return true;
	}

	@Override
	protected void populateValues(final DataType sample) {
		sample.put("durationIsSeconds", random.nextInt(60 * 60));
		sample.put("caller", createPstnEndpoint("pstn", randomSample(pstnNumbers)));
		sample.put("called", createPstnEndpoint("pstn", randomSample(pstnNumbers)));

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