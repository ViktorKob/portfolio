package net.thomas.portfolio.hbase_index.fake.generators;

import java.util.Date;
import java.util.GregorianCalendar;

import net.thomas.portfolio.hbase_index.fake.generators.primitives.TimestampGenerator;
import net.thomas.portfolio.hbase_index.schema.documents.DocumentEntity;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public abstract class DocumentEntityGenerator<TYPE extends DocumentEntity> extends EntityGenerator<TYPE> {

	private final TimestampGenerator timestampGenerator;
	// private final Timestamp today;

	public DocumentEntityGenerator(long randomSeed) {
		super(false, randomSeed);
		final Date startDate = new GregorianCalendar(2017, 4, 17).getTime();
		final Date endDate = new GregorianCalendar(2017, 10, 17).getTime();
		// today = new Timestamp(endDate.getTime());
		timestampGenerator = new TimestampGenerator(startDate, endDate, random.nextLong());
	}

	private Timestamp generateTimestamp() {
		return new Timestamp(timestampGenerator.generate());
	}
	//
	// private void populateDates(Document sample) {
	// final Timestamp timeOfEvent = ;
	// sample.setTimeOfEvent(timeOfEvent);
	// final Timestamp timeOfInterception = new Timestamp(timeOfEvent.getTimestamp() + (long) (random.nextDouble() * (1000l * 60l * 60l * 24l * 90l)));
	// sample.setTimeOfInterception(timeOfInterception.isBefore(today) ? timeOfInterception : today);
	// }
}
