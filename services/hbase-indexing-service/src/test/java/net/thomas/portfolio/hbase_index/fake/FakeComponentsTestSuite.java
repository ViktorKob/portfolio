package net.thomas.portfolio.hbase_index.fake;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.fake.generators.GeneratorTestSuite;
import net.thomas.portfolio.hbase_index.fake.processing_steps.FakeInvertedIndexStepUnitTest;
import net.thomas.portfolio.hbase_index.fake.processing_steps.FakeSelectorStatisticsStepUnitTest;
import net.thomas.portfolio.hbase_index.fake.world.WorldIoControlIntegrationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FakeHbaseIndexUnitTest.class, GeneratorTestSuite.class, FakeInvertedIndexStepUnitTest.class,
		FakeSelectorStatisticsStepUnitTest.class, WorldIoControlIntegrationTest.class })
public class FakeComponentsTestSuite {
}