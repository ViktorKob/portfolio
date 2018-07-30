package net.thomas.portfolio.hbase_index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.fake.generators.GeneratorTestSuite;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityVisitorAlgorithmsUnitTest;
import net.thomas.portfolio.hbase_index.service.HbaseIndexingServiceControllerServiceAdaptorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ HbaseIndexingServiceControllerServiceAdaptorTest.class, EntityVisitorAlgorithmsUnitTest.class, GeneratorTestSuite.class })
public class HbaseIndexingServiceTestSuite {
}