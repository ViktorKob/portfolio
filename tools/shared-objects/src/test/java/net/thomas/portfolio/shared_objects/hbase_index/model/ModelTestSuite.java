package net.thomas.portfolio.shared_objects.hbase_index.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.TypesTestSuite;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.UtilsTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TypesTestSuite.class, UtilsTestSuite.class })
public class ModelTestSuite {
}