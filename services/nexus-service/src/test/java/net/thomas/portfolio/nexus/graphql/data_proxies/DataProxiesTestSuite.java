package net.thomas.portfolio.nexus.graphql.data_proxies;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DataTypeProxyUnitTest.class, DataTypeEntityProxyUnitTest.class, DataTypeIdProxyUnitTest.class, DocumentEntityProxyUnitTest.class,
		DocumentIdProxyUnitTest.class, DocumentInfoProxyUnitTest.class, RawTypeEntityProxyUnitTest.class, RawTypeIdProxyUnitTest.class,
		SelectorEntityProxyUnitTest.class, SelectorIdProxyUnitTest.class })
public class DataProxiesTestSuite {
}