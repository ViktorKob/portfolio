package net.thomas.portfolio.hbase_index.fake.generators;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeType;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.UidGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public abstract class DataTypeGenerator implements Iterable<DataType>, Iterator<DataType> {

	protected final String dataTypeName;
	protected final Random random;
	private final UidGenerator uidTool;

	public DataTypeGenerator(String dataTypeName, boolean keyShouldBeUnique, HbaseIndexSchema schema, long randomSeed) {
		this.dataTypeName = dataTypeName;
		random = new Random(randomSeed);
		uidTool = new UidGenerator(schema.getFieldsForDataType(dataTypeName), keyShouldBeUnique);
	}

	protected synchronized void populateUid(DataType sample) {
		sample.setUid(uidTool.calculateUid(sample));
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public DataType next() {
		final DataType sample = new DataType(getDataTypeType(), dataTypeName);
		populateFields(sample);
		return sample;
	}

	protected void populateFields(DataType sample) {
		populateValues(sample);
		populateUid(sample);
	}

	protected DataType randomSample(List<DataType> values) {
		return values.get(random.nextInt(values.size()));
	}

	protected abstract DataTypeType getDataTypeType();

	protected abstract boolean keyShouldBeUnique();

	protected abstract void populateValues(DataType sample);

	@Override
	public Iterator<DataType> iterator() {
		return this;
	}
}
