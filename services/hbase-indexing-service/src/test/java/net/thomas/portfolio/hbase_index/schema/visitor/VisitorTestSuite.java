package net.thomas.portfolio.hbase_index.schema.visitor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ LocalnameVisitorAlgorithmsUnitTest.class, DomainVisitorAlgorithmsUnitTest.class, EmailAddressVisitorAlgorithmsUnitTest.class,
		DisplayedNameVisitorAlgorithmsUnitTest.class, EmailEndpointVisitorAlgorithmsUnitTest.class, EmailVisitorAlgorithmsUnitTest.class })
public class VisitorTestSuite {
}