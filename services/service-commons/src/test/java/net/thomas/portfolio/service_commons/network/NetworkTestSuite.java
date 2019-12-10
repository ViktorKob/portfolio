package net.thomas.portfolio.service_commons.network;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.service_commons.network.urls.UrlsTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ HttpRestClientUnitTest.class, UrlsTestSuite.class })
public class NetworkTestSuite {
}
