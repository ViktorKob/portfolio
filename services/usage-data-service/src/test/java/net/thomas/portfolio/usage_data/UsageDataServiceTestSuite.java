package net.thomas.portfolio.usage_data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.usage_data.service.UsageDataServiceControllerServiceAdaptorTest;
import net.thomas.portfolio.usage_data.sql.UsageDataSqlProxyIntegrationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ UsageDataSqlProxyIntegrationTest.class, UsageDataServiceControllerServiceAdaptorTest.class })
public class UsageDataServiceTestSuite {
}