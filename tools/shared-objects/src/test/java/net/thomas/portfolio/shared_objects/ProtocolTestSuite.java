package net.thomas.portfolio.shared_objects;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledgeUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.request.BoundsUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequestUnitTest;
import net.thomas.portfolio.shared_objects.legal.LegalTestSuite;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivitiesUnitTest;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ LegalTestSuite.class, BoundsUnitTest.class, InvertedIndexLookupRequestUnitTest.class, AnalyticalKnowledgeUnitTest.class,
		UsageActivityUnitTest.class, UsageActivitiesUnitTest.class })
public class ProtocolTestSuite {
}