package net.thomas.portfolio.analytics;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.analytics.service.AnalyticsServiceControllerServiceAdaptorTest;
import net.thomas.portfolio.analytics.system.AnalyticsControlUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AnalyticsControlUnitTest.class, AnalyticsServiceControllerServiceAdaptorTest.class })
public class AnalyticsServiceTestSuite {
}