package net.thomas.portfolio.hbase_index.fake.generators;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.UidGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public abstract class DataTypeGenerator implements Iterable<Datatype>, Iterator<Datatype> {

	protected final String dataTypeName;
	protected final Random random;
	private final UidGenerator uidTool;

	public DataTypeGenerator(String dataTypeName, boolean keyShouldBeUnique, HbaseIndexSchema schema, long randomSeed) {
		this.dataTypeName = dataTypeName;
		random = new Random(randomSeed);
		uidTool = new UidGenerator(schema.getFieldsForDataType(dataTypeName), keyShouldBeUnique);
	}

	protected synchronized void populateUid(Datatype sample) {
		sample.setUid(uidTool.calculateUid(sample));
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Datatype next() {
		final Datatype sample = new Datatype(dataTypeName);
		populateFields(sample);
		return sample;
	}

	protected void populateFields(Datatype sample) {
		populateValues(sample);
		populateUid(sample);
	}

	protected Datatype randomSample(List<Datatype> values) {
		return values.get(random.nextInt(values.size()));
	}

	protected abstract boolean keyShouldBeUnique();

	protected abstract void populateValues(Datatype sample);

	@Override
	public Iterator<Datatype> iterator() {
		return this;
	}
}
