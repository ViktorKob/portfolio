package net.thomas.portfolio.hbase_index.fake.generators.documents;

import java.util.List;

import net.thomas.portfolio.hbase_index.fake.FakeWorld.Person;
import net.thomas.portfolio.hbase_index.fake.generators.EventGenerator;
import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class ConversationGenerator extends EventGenerator<Conversation> {
	private final Person initiator;
	private final List<Person> personalRelations;

	public ConversationGenerator(Person initiator, List<Person> personalRelations, long randomSeed) {
		super(randomSeed);
		this.initiator = initiator;
		this.personalRelations = personalRelations;
	}

	@Override
	protected Conversation createInstance() {
		final Timestamp timeOfEvent = generateTimeOfEvent();
		final Timestamp timeOfInterception = generateTimeOfInterception(timeOfEvent);
		final int durationIsSeconds = random.nextInt(60 * 60);
		final CommunicationEndpoint primary = new CommunicationEndpoint(randomProgressiveSample(initiator.publicIdNumbers), null);
		primary.uid = idTool.calculate(primary);
		final CommunicationEndpoint secondary = new CommunicationEndpoint(null,
				randomProgressiveSample(randomProgressiveSample(personalRelations).privateIdNumbers));
		secondary.uid = idTool.calculate(secondary);
		final GeoLocation senderLocation = randomLocation(0.5);
		final GeoLocation receiverLocation = randomLocation(0.5);
		return new Conversation(durationIsSeconds, primary, secondary, senderLocation, receiverLocation, timeOfEvent, timeOfInterception);
	}
}