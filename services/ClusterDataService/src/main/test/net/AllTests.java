package net;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.sample.SampleDataSetGeneratorDocumentSanityTest;
import net.sample.SampleDataSetGeneratorSelectorSanityTest;
import net.sample.SampleStorageTest;
import net.sample.generators.DataTypeGeneratorTest;
import net.sample.generators.NameGeneratorTest;

@RunWith(Suite.class)
@SuiteClasses({ SampleStorageTest.class, DataTypeGeneratorTest.class, NameGeneratorTest.class, SampleDataSetGeneratorDocumentSanityTest.class,
		SampleDataSetGeneratorSelectorSanityTest.class })
public class AllTests {}
