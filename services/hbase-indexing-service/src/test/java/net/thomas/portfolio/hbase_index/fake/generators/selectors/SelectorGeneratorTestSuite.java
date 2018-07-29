package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DisplayedNameGeneratorUnitTest.class, LocalnameGeneratorUnitTest.class, PublicIdGeneratorUnitTest.class, PrivateIdGeneratorUnitTest.class,
		DomainGeneratorUnitTest.class, EmailAddressGeneratorUnitTest.class })
public class SelectorGeneratorTestSuite {
}