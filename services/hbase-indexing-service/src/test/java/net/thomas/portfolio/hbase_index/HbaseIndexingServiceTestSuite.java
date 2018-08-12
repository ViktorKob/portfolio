package net.thomas.portfolio.hbase_index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndexUnitTest;
import net.thomas.portfolio.hbase_index.fake.generators.GeneratorTestSuite;
import net.thomas.portfolio.hbase_index.lookup.InvertedIndexLookupAndBuilderUnitTest;
import net.thomas.portfolio.hbase_index.schema.SchemaTestSuite;
import net.thomas.portfolio.hbase_index.service.HbaseIndexingServiceControllerServiceAdaptorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FakeHbaseIndexUnitTest.class, SchemaTestSuite.class, GeneratorTestSuite.class, HbaseIndexingServiceControllerServiceAdaptorTest.class,
		InvertedIndexLookupAndBuilderUnitTest.class })
public class HbaseIndexingServiceTestSuite {
}