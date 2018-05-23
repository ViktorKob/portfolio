package net.thomas.portfolio.hbase_index.fake.generators;

import static net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeType.SELECTOR;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public abstract class SelectorGenerator extends DataTypeGenerator {

	public SelectorGenerator(String dataTypeName, HbaseIndexSchema schema, long randomSeed) {
		super(dataTypeName, false, schema, randomSeed);
	}

	@Override
	protected DataTypeType getDataTypeType() {
		return SELECTOR;
	}

	@Override
	public DataType next() {
		final Selector sample = new Selector(dataTypeName);
		populateFields(sample);
		return sample;
	}
}
