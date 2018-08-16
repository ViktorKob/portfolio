package net.thomas.portfolio.nexus.graphql.fetchers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.nexus.graphql.fetchers.data_types.DataTypeFetcherUnitTest;
import net.thomas.portfolio.nexus.graphql.fetchers.data_types.DocumentFetcherUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DataTypeFetcherUnitTest.class, DocumentFetcherUnitTest.class })
public class FetchersTestSuite {
}