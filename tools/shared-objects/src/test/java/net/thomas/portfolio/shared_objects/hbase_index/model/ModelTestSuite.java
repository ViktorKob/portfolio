package net.thomas.portfolio.shared_objects.hbase_index.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverterUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.IdCalculatorUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.UidConverterUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DateConverterUnitTest.class, UidConverterUnitTest.class, IdCalculatorUnitTest.class })
public class ModelTestSuite {
}