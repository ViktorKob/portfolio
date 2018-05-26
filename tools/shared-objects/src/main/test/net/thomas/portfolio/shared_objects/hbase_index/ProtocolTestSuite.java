package net.thomas.portfolio.shared_objects.hbase_index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeIdUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataTypeUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.SelectorUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupUnitTest;
import net.thomas.portfolio.shared_objects.legal.LegalInformationUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DataTypeIdUnitTest.class, RawDataTypeUnitTest.class, SelectorUnitTest.class, DocumentUnitTest.class, InvertedIndexLookupUnitTest.class,
		LegalInformationUnitTest.class })
public class ProtocolTestSuite {
}