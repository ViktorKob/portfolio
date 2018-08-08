package net.thomas.portfolio.hbase_index.fake.generators;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.hbase_index.fake.generators.selectors.SelectorGeneratorTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ EntityGeneratorUnitTest.class, EventGeneratorUnitTest.class, SelectorGeneratorTestSuite.class })
public class GeneratorTestSuite {
}