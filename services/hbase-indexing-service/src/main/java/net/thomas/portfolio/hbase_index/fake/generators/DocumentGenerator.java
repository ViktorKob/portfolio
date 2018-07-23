package net.thomas.portfolio.hbase_index.fake.generators;

import java.util.Date;
import java.util.GregorianCalendar;

import net.thomas.portfolio.hbase_index.fake.generators.primitives.TimestampGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public abstract class DocumentGenerator extends DataTypeGenerator {

	private final TimestampGenerator timestampGenerator;
	private final Timestamp today;

	public DocumentGenerator(String dataTypeName, HbaseIndexSchema schema, long randomSeed) {
		super(dataTypeName, true, schema, randomSeed);
		final Date startDate = new GregorianCalendar(2017, 4, 17).getTime();
		final Date endDate = new GregorianCalendar(2017, 10, 17).getTime();
		today = new Timestamp(endDate.getTime());
		timestampGenerator = new TimestampGenerator(startDate, endDate, random.nextLong());
	}

	@Override
	public DataType next() {
		final Document sample = new Document();
		populateFields(sample);
		populateDates(sample);
		return sample;
	}

	private void populateDates(Document sample) {
		final Timestamp timeOfEvent = new Timestamp(timestampGenerator.generate());
		sample.setTimeOfEvent(timeOfEvent);
		final Timestamp timeOfInterception = new Timestamp(timeOfEvent.getTimestamp() + (long) (random.nextDouble() * (1000l * 60l * 60l * 24l * 90l)));
		sample.setTimeOfInterception(timeOfInterception.isBefore(today) ? timeOfInterception : today);
	}
}
