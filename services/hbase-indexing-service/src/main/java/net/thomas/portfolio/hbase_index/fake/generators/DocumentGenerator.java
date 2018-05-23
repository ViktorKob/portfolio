package net.thomas.portfolio.hbase_index.fake.generators;

import static net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeType.DOCUMENT;

import java.util.Date;
import java.util.GregorianCalendar;

import net.thomas.portfolio.hbase_index.fake.generators.primitives.TimestampGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public abstract class DocumentGenerator extends DataTypeGenerator {

	private final TimestampGenerator timestampGenerator;
	private final long today;

	public DocumentGenerator(String dataTypeName, HbaseIndexSchema schema, long randomSeed) {
		super(dataTypeName, true, schema, randomSeed);
		final Date startDate = new GregorianCalendar(2017, 4, 17).getTime();
		final Date endDate = new GregorianCalendar(2017, 10, 17).getTime();
		today = endDate.getTime();
		timestampGenerator = new TimestampGenerator(startDate, endDate, random.nextLong());
	}

	@Override
	protected DataTypeType getDataTypeType() {
		return DOCUMENT;
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
