package net.thomas.portfolio.hbase_index.fake.generators.documents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.hbase_index.fake.generators.DocumentGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.UidGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class VoiceGenerator extends DocumentGenerator {
	private final List<Datatype> pstnNumbers;
	private final UidGenerator uidTool;

	public VoiceGenerator(Map<String, Datatype> pstnNumbers, Map<String, Datatype> imsiNumbers, HbaseIndexSchema schema, long randomSeed) {
		super("Voice", schema, randomSeed);
		this.pstnNumbers = new ArrayList<>(pstnNumbers.values());
		uidTool = new UidGenerator(schema.getFieldsForDataType("PstnEndpoint"), true);
	}

	@Override
	protected final boolean keyShouldBeUnique() {
		return true;
	}

	@Override
	protected void populateValues(final Datatype sample) {
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

	private Datatype createPstnEndpoint(String numberField, Datatype number) {
		final Datatype endpoint = new Datatype("PstnEndpoint");
		endpoint.put(numberField, number);
		endpoint.setUid(uidTool.calculateUid(endpoint));
		return endpoint;
	}
}