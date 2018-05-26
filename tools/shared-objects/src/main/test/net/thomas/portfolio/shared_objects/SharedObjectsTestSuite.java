package net.thomas.portfolio.shared_objects;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.hbase_index.ProtocolTestSuite;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.UidConverterUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ProtocolTestSuite.class, UidConverterUnitTest.class })
public class SharedObjectsTestSuite {

}
