package net.thomas.portfolio.hbase_index.fake.generators.documents;

import java.util.List;

import net.thomas.portfolio.hbase_index.fake.FakeWorld.Person;
import net.thomas.portfolio.hbase_index.fake.generators.DocumentGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.StringGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class TextMessageGenerator extends DocumentGenerator {
	private final StringGenerator messageGenerator;
	private final IdCalculator uidTool;
	private final Person initiator;
	private final List<Person> personalRelations;

	public TextMessageGenerator(Person initiator, List<Person> personalRelations, HbaseIndexSchema schema, long randomSeed) {
		super("TextMessage", schema, randomSeed);
		this.initiator = initiator;
		this.personalRelations = personalRelations;
		messageGenerator = new StringGenerator(5, 250, 0.1, random.nextLong());
		uidTool = new IdCalculator(schema.getFieldsForDataType("CommunicationEndpoint"), true);
	}

	@Override
	protected final boolean keyShouldBeUnique() {
		return false;
	}

	@Override
	protected void populateValues(final DataType sample) {
		sample.put("message", messageGenerator.generate());
		sample.put("sender", createCommunicationEndpoint("publicId", randomProgressiveSample(initiator.publicIdNumbers)));
		sample.put("receiver", createCommunicationEndpoint("privateId", randomProgressiveSample(randomProgressiveSample(personalRelations).privateIdNumbers)));

		if (random.nextDouble() < 0.5) {
			sample.put("senderLocation", new GeoLocation(random.nextDouble() * 360 - 180, random.nextDouble() * 180 - 90));
		}
		if (random.nextDouble() < 0.5) {
			sample.put("receiverLocation", new GeoLocation(random.nextDouble() * 360 - 180, random.nextDouble() * 180 - 90));
		}
	}

	private DataType createCommunicationEndpoint(String numberField, DataType number) {
		final DataType endpoint = new RawDataType();
		endpoint.put(numberField, number);
		endpoint.setId(uidTool.calculate("CommunicationEndpoint", endpoint));
		return endpoint;
	}
}