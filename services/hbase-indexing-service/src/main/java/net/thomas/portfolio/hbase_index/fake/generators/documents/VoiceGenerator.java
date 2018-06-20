package net.thomas.portfolio.hbase_index.fake.generators.documents;

import java.util.List;

import net.thomas.portfolio.hbase_index.fake.generators.DocumentGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.World.Person;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.IdGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class VoiceGenerator extends DocumentGenerator {
	private final IdGenerator uidTool;
	private final Person initiator;
	private final List<Person> personalRelations;

	public VoiceGenerator(Person initiator, List<Person> personalRelations, HbaseIndexSchema schema, long randomSeed) {
		super("Voice", schema, randomSeed);
		this.initiator = initiator;
		this.personalRelations = personalRelations;
		uidTool = new IdGenerator(schema.getFieldsForDataType("PstnEndpoint"), true);
	}

	@Override
	protected final boolean keyShouldBeUnique() {
		return false;
	}

	@Override
	protected void populateValues(final DataType sample) {
		sample.put("durationIsSeconds", random.nextInt(60 * 60));
		sample.put("caller", createPstnEndpoint("pstn", randomProgressiveSample(initiator.pstnNumbers)));
		sample.put("called", createPstnEndpoint("pstn", randomProgressiveSample(randomProgressiveSample(personalRelations).pstnNumbers)));

		if (random.nextDouble() < 0.5) {
			sample.put("senderLocation", new GeoLocation(random.nextDouble() * 360 - 180, random.nextDouble() * 180 - 90));
		}
		if (random.nextDouble() < 0.5) {
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