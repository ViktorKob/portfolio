package net.thomas.portfolio.hbase_index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.fake.generators.GeneratorTestSuite;
import net.thomas.portfolio.hbase_index.schema.visitor.VisitorTestSuite;
import net.thomas.portfolio.hbase_index.service.HbaseIndexingServiceControllerServiceAdaptorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ HbaseIndexingServiceControllerServiceAdaptorTest.class, VisitorTestSuite.class, GeneratorTestSuite.class })
public class HbaseIndexingServiceTestSuite {
}