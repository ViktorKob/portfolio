package net.thomas.portfolio.hbase_index.fake.generators;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.fake.generators.selectors.DisplayedNameGeneratorUnitTest;
import net.thomas.portfolio.hbase_index.fake.generators.selectors.LocalnameGeneratorUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ EntityGeneratorUnitTest.class, DisplayedNameGeneratorUnitTest.class, LocalnameGeneratorUnitTest.class })
public class GeneratorTestSuite {

}
