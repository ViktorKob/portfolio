package net.thomas.portfolio.nexus.graphql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgumentParametizedUnitTest;
import net.thomas.portfolio.nexus.graphql.data_proxies.DataProxiesTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DataProxiesTestSuite.class, GraphQlArgumentParametizedUnitTest.class })
public class GraphQlModelTestSuite {

}