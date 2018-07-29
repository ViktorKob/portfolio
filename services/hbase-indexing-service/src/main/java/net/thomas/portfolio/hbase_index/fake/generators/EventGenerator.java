package net.thomas.portfolio.hbase_index.fake.generators;

import java.util.Date;
import java.util.GregorianCalendar;

import net.thomas.portfolio.hbase_index.fake.generators.primitives.TimestampGenerator;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public abstract class EventGenerator<TYPE extends Event> extends EntityGenerator<TYPE> {

	private final TimestampGenerator timestampGenerator;
	private final Timestamp today;

	public EventGenerator(long randomSeed) {
		super(false, randomSeed);
		final Date startDate = new GregorianCalendar(2017, 4, 17).getTime();
		final Date endDate = new GregorianCalendar(2017, 10, 17).getTime();
		today = new Timestamp(endDate.getTime());
		timestampGenerator = new TimestampGenerator(startDate, endDate, random.nextLong());
	}

	protected Timestamp generateTimeOfEvent() {
		return new Timestamp(timestampGenerator.generate());
	}

	protected Timestamp generateTimeOfInterception(Timestamp timeOfEvent) {
		final Timestamp timeOfInterception = new Timestamp(timeOfEvent.getTimestamp() + (long) (random.nextDouble() * (1000l * 60l * 60l * 24l * 90l)));
		return timeOfInterception.isBefore(today) ? timeOfInterception : today;
	}

	protected GeoLocation randomLocation(double probabilityOfExistance) {
		final GeoLocation location;
		if (random.nextDouble() < probabilityOfExistance) {
			location = new GeoLocation(random.nextDouble() * 360 - 180, random.nextDouble() * 180 - 90);
		} else {
			location = null;
		}
		return location;
	}

}
