package net.thomas.portfolio.shared_objects.hbase_index.transformation;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public interface IndexControl {

	void setSchema(HbaseIndexSchema schema);

	void index(World world);

	HbaseIndexSchema getSchema();

	HbaseIndex getIndex();

}
