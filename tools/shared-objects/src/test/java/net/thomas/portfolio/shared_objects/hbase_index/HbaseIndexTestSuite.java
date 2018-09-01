package net.thomas.portfolio.shared_objects.hbase_index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.shared_objects.hbase_index.model.ModelTestSuite;
import net.thomas.portfolio.shared_objects.hbase_index.request.BoundsUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequestUnitTest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaAndBuilderUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ModelTestSuite.class, BoundsUnitTest.class, InvertedIndexLookupRequestUnitTest.class, HbaseIndexSchemaAndBuilderUnitTest.class })
public class HbaseIndexTestSuite {
}