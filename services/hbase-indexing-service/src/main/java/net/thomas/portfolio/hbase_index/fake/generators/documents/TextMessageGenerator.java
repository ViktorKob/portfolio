package net.thomas.portfolio.hbase_index.fake.generators.documents;

import java.util.List;

import net.thomas.portfolio.hbase_index.fake.FakeWorld.Person;
import net.thomas.portfolio.hbase_index.fake.generators.EventGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.StringGenerator;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class TextMessageGenerator extends EventGenerator<TextMessage> {
	private final StringGenerator messageGenerator;
	private final Person initiator;
	private final List<Person> personalRelations;

	public TextMessageGenerator(Person initiator, List<Person> personalRelations, long randomSeed) {
		super(randomSeed);
		this.initiator = initiator;
		this.personalRelations = personalRelations;
		messageGenerator = new StringGenerator(5, 250, 0.1, random.nextLong());
	}

	@Override
	protected TextMessage createInstance() {
		final Timestamp timeOfEvent = generateTimeOfEvent();
		final Timestamp timeOfInterception = generateTimeOfInterception(timeOfEvent);
		final String message = messageGenerator.generate();
		final CommunicationEndpoint sender = createCommunicationEndpoint(randomProgressiveSample(initiator.publicIdNumbers), null);
		sender.uid = idTool.calculate(sender);
		final CommunicationEndpoint receiver = createCommunicationEndpoint(null,
				randomProgressiveSample(randomProgressiveSample(personalRelations).privateIdNumbers));
		receiver.uid = idTool.calculate(receiver);
		final GeoLocation senderLocation = randomLocation(0.5);
		final GeoLocation receiverLocation = randomLocation(0.5);
		return new TextMessage(message, sender, receiver, senderLocation, receiverLocation, timeOfEvent, timeOfInterception);
	}

	private CommunicationEndpoint createCommunicationEndpoint(PublicId publicId, PrivateId privateId) {
		return new CommunicationEndpoint(publicId, privateId);
	}
}