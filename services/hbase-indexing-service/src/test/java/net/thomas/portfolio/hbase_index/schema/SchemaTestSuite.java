package net.thomas.portfolio.hbase_index.schema;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.schema.visitor.VisitorTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ EventProtocolUnitTest.class, VisitorTestSuite.class })
public class SchemaTestSuite {
}