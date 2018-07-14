package net.thomas.portfolio.hbase_index.fake.generators;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public abstract class DataTypeGenerator implements Iterable<DataType>, Iterator<DataType> {

	protected final String dataTypeName;
	protected final Random random;
	private final IdCalculator idTool;

	public DataTypeGenerator(String dataTypeName, boolean keyShouldBeUnique, HbaseIndexSchema schema, long randomSeed) {
		this.dataTypeName = dataTypeName;
		random = new Random(randomSeed);
		idTool = new IdCalculator(schema.getFieldsForDataType(dataTypeName), keyShouldBeUnique);
	}

	protected synchronized void populateUid(DataType sample) {
		sample.setId(idTool.calculate(dataTypeName, sample));
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public DataType next() {
		final DataType sample = new RawDataType();
		populateFields(sample);
		return sample;
	}

	protected void populateFields(DataType sample) {
		populateValues(sample);
		populateUid(sample);
	}

	protected <T> T randomSample(List<T> values) {
		return values.get(random.nextInt(values.size()));
	}

	protected <T> T randomProgressiveSample(List<T> values) {
		for (int i = 0; i < values.size() - 1; i++) {
			if (random.nextDouble() < 0.5d) {
				return values.get(i);
			}
		}
		return values.get(values.size() - 1);
	}

	protected abstract boolean keyShouldBeUnique();

	protected abstract void populateValues(DataType sample);

	@Override
	public Iterator<DataType> iterator() {
		return this;
	}
}
