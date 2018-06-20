package net.thomas.portfolio.hbase_index.fake.generators.documents;

import java.util.List;

import net.thomas.portfolio.hbase_index.fake.FakeWorldInitializer.Person;
import net.thomas.portfolio.hbase_index.fake.generators.DocumentGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.StringGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.IdGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class SmsGenerator extends DocumentGenerator {
	private final StringGenerator messageGenerator;
	private final IdGenerator uidTool;
	private final Person initiator;
	private final List<Person> personalRelations;

	public SmsGenerator(Person initiator, List<Person> personalRelations, HbaseIndexSchema schema, long randomSeed) {
		super("Sms", schema, randomSeed);
		this.initiator = initiator;
		this.personalRelations = personalRelations;
		messageGenerator = new StringGenerator(5, 250, 0.1, random.nextLong());
		uidTool = new IdGenerator(schema.getFieldsForDataType("PstnEndpoint"), true);
	}

	@Override
	protected final boolean keyShouldBeUnique() {
		return false;
	}

	@Override
	protected void populateValues(final DataType sample) {
		sample.put("message", messageGenerator.generate());
		sample.put("sender", createPstnEndpoint("pstn", randomProgressiveSample(initiator.pstnNumbers)));
		sample.put("receiver", createPstnEndpoint("imsi", randomProgressiveSample(randomProgressiveSample(personalRelations).imsiNumbers)));

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