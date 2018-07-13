package net.thomas.portfolio.shared_objects.hbase_index.schema;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.DomainSimpleRepParserUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.EmailAddressSimpleRepParserUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.PositiveIntegerFieldSimpleRepParserUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.StringFieldSimpleRepParserUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParserLibraryBuilderUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParserUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ StringFieldSimpleRepParserUnitTest.class, PositiveIntegerFieldSimpleRepParserUnitTest.class, DomainSimpleRepParserUnitTest.class,
		EmailAddressSimpleRepParserUnitTest.class, SimpleRepresentationParserLibraryBuilderUnitTest.class, SimpleRepresentationParserUnitTest.class })
public class SchemaTestSuite {
}