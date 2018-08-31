package net.thomas.portfolio.hbase_index.schema;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.DomainSimpleRepParserUnitTest;
import net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.EmailAddressSimpleRepParserUnitTest;
import net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.PositiveIntegerFieldSimpleRepParserUnitTest;
import net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.StringFieldSimpleRepParserUnitTest;
import net.thomas.portfolio.hbase_index.schema.util.IdCalculatorUnitTest;
import net.thomas.portfolio.hbase_index.schema.util.SimpleRepresentationParserUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ IdCalculatorUnitTest.class, StringFieldSimpleRepParserUnitTest.class, PositiveIntegerFieldSimpleRepParserUnitTest.class,
		DomainSimpleRepParserUnitTest.class, EmailAddressSimpleRepParserUnitTest.class, SimpleRepresentationParserUnitTest.class,
		HbaseIndexSchemaAndSchemaBuilderUnitTest.class })
public class SchemaTestSuite {
}