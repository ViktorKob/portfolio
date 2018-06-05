package net.thomas.portfolio.hbase_index.fake.generators.documents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.hbase_index.fake.generators.DocumentGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.IdGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class VoiceGenerator extends DocumentGenerator {
	private final List<DataType> pstnNumbers;
	private final IdGenerator uidTool;

	public VoiceGenerator(Map<String, DataType> pstnNumbers, Map<String, DataType> imsiNumbers, HbaseIndexSchema schema, long randomSeed) {
		super("Voice", schema, randomSeed);
		this.pstnNumbers = new ArrayList<>(pstnNumbers.values());
		uidTool = new IdGenerator(schema.getFieldsForDataType("PstnEndpoint"), true);
	}

	@Override
	protected final boolean keyShouldBeUnique() {
		return false;
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
		final DataType endpoint = new RawDataType();
		endpoint.put(numberField, number);
		endpoint.setId(uidTool.calculateId("PstnEndpoint", endpoint));
		return endpoint;
	}
}