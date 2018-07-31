package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ LocalnameVisitorAlgorithmsUnitTest.class, DisplayedNameVisitorAlgorithmsUnitTest.class, PublicIdVisitorAlgorithmsUnitTest.class,
		PrivateIdVisitorAlgorithmsUnitTest.class, DomainVisitorAlgorithmsUnitTest.class, EmailAddressVisitorAlgorithmsUnitTest.class,
		EmailEndpointVisitorAlgorithmsUnitTest.class, CommunicationEndpointVisitorAlgorithmsUnitTest.class, EmailVisitorAlgorithmsUnitTest.class,
		TextMessageVisitorAlgorithmsUnitTest.class, ConversationVisitorAlgorithmsUnitTest.class })
public class VisitorTestSuite {
}