package net.sample.generators;

import static net.sample.SampleModel.DATA_TYPE_FIELDS;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.model.DataType;
import net.model.util.UidTool;

public abstract class DataTypeGenerator implements Iterable<DataType>, Iterator<DataType> {

	protected final String dataTypeName;
	protected final Random random;
	private final UidTool uidTool;

	public DataTypeGenerator(String dataTypeName, boolean keyShouldBeUnique, long randomSeed) {
		this.dataTypeName = dataTypeName;
		random = new Random(randomSeed);
		uidTool = new UidTool(DATA_TYPE_FIELDS.get(dataTypeName).values(), keyShouldBeUnique);
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
		final DataType sample = new DataType(dataTypeName);
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

	protected abstract boolean keyShouldBeUnique();

	protected abstract void populateValues(DataType sample);

	@Override
	public Iterator<DataType> iterator() {
		return this;
	}
}
