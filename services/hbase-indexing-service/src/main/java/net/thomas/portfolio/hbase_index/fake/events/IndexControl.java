package net.thomas.portfolio.hbase_index.fake.events;

import net.thomas.portfolio.hbase_index.schema.simple_rep.SimpleRepresentationParserLibrary;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public interface IndexControl {

	HbaseIndexSchema getSchema();

	HbaseIndex getIndex();

	SimpleRepresentationParserLibrary getSimpleRepresentationParserLibrary();

}
