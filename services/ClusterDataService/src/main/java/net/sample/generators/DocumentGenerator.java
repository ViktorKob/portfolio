package net.sample.generators;

import java.util.Date;
import java.util.GregorianCalendar;

import net.model.DataType;
import net.model.types.Document;
import net.sample.generators.primitives.TimestampGenerator;

public abstract class DocumentGenerator extends DataTypeGenerator {

	private final TimestampGenerator timestampGenerator;
	private final long today;

	public DocumentGenerator(String dataTypeName, long randomSeed) {
		super(dataTypeName, true, randomSeed);
		final Date startDate = new GregorianCalendar(2017, 4, 17).getTime();
		final Date endDate = new GregorianCalendar(2017, 10, 17).getTime();
		today = endDate.getTime();
		timestampGenerator = new TimestampGenerator(startDate, endDate, random.nextLong());
	}

	@Override
	public DataType next() {
		final Document sample = new Document(dataTypeName);
		populateDates(sample);
		populateFields(sample);
		return sample;
	}

	private void populateDates(Document sample) {
		final long timeOfEvent = timestampGenerator.generate();
		sample.setTimeOfEvent(timeOfEvent);
		final long timeOfInterception = timeOfEvent + (long) (random.nextDouble() * (1000l * 60l * 60l * 24l * 90l));
		sample.setTimeOfInterception(timeOfInterception < today ? timeOfInterception : today);
	}
}
