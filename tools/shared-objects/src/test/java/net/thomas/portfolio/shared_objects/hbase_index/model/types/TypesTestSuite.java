package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DataTypeUnitTest.class, DataTypeIdUnitTest.class, DocumentUnitTest.class, DocumentInfoUnitTest.class, GeoLocationUnitTest.class,
		RawDataTypeUnitTest.class, SelectorUnitTest.class, TimestampUnitTest.class })
public class TypesTestSuite {
}