package net.thomas.portfolio.hbase_index.service;

import static java.nio.file.Paths.get;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class FakeIndexControlUnitTest {

	@Before
	public void setUpForTest() {
		final File outputFile = get(".", "src", "main", "resources", "data", "world.json.gzip").toFile();
		outputFile.delete();
		control = new FakeIndexControl(CONFIG);
	}

	@Test
	public void shouldInitializeWorldWhenAskingForSchema() {
		final HbaseIndexSchema schema = control.getSchema();
		final Collection<String> samples = schema.getDocumentTypes();
		assertTrue(samples.contains("Email"));
	}

	@Test
	public void shouldInitializeWorldWhenAskingForIndex() {
		final HbaseIndex index = control.getIndex();
		final Entities samples = index.getSamples("Email", 1);
		assertTrue(samples.hasData());
	}

	private static final HbaseIndexingServiceConfiguration CONFIG = new HbaseIndexingServiceConfiguration();
	private static FakeIndexControl control;
	static {
		CONFIG.setRandomSeed(1234L);
		CONFIG.setPopulationCount(10);
		CONFIG.setAverageRelationCount(5);
		CONFIG.setAverageCommunicationCount(20);
	}
}
