package net.thomas.portfolio.shared_objects;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledgeUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeIdUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataTypeUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.SelectorUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.request.BoundsUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequestUnitTest;
import net.thomas.portfolio.shared_objects.legal.LegalInformationUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DataTypeIdUnitTest.class, RawDataTypeUnitTest.class, SelectorUnitTest.class, DocumentUnitTest.class, LegalInformationUnitTest.class,
		BoundsUnitTest.class, InvertedIndexLookupRequestUnitTest.class, AnalyticalKnowledgeUnitTest.class })
public class ProtocolTestSuite {
}