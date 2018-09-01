package net.thomas.portfolio.shared_objects;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.hbase_index.HbaseIndexTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ HbaseIndexTestSuite.class, ProtocolTestSuite.class })
public class SharedObjectsTestSuite {
}