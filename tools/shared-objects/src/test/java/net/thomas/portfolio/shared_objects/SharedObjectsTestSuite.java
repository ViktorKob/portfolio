package net.thomas.portfolio.shared_objects;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.hbase_index.model.ModelTestSuite;
import net.thomas.portfolio.shared_objects.hbase_index.schema.SchemaTestSuite;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.IdCalculatorUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ IdCalculatorUnitTest.class, ModelTestSuite.class, SchemaTestSuite.class, ProtocolTestSuite.class })
public class SharedObjectsTestSuite {
}