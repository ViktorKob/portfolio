package net.thomas.portfolio.nexus;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgumentParametizedUnitTest;
import net.thomas.portfolio.nexus.service.NexusServiceControllerServiceAdaptorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ GraphQlArgumentParametizedUnitTest.class, NexusServiceControllerServiceAdaptorTest.class })
public class NexusServiceTestSuite {
}