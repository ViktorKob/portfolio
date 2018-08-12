package net.thomas.portfolio.hbase_index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.fake.FakeComponentsTestSuite;
import net.thomas.portfolio.hbase_index.lookup.InvertedIndexLookupAndBuilderUnitTest;
import net.thomas.portfolio.hbase_index.schema.SchemaTestSuite;
import net.thomas.portfolio.hbase_index.service.HbaseIndexingServiceControllerServiceAdaptorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FakeComponentsTestSuite.class, SchemaTestSuite.class, HbaseIndexingServiceControllerServiceAdaptorTest.class,
		InvertedIndexLookupAndBuilderUnitTest.class })
public class HbaseIndexingServiceTestSuite {
}