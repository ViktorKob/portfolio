package net.thomas.portfolio.hbase_index.schema.processing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ Entity2DataTypeConverterUnitTest.class, SchemaIntrospectionUnitTest.class, VisitorTestSuite.class })
public class ProcessingTestSuite {
}