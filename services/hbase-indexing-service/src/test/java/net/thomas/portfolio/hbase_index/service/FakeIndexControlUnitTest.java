package net.thomas.portfolio.hbase_index.service;

import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static org.assertj.core.util.Files.delete;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class FakeIndexControlUnitTest {
	@BeforeClass
	public static void setUpFileSystem() throws IOException {
		final Path outputFolder = get(STORAGE_ROOT);
		delete(outputFolder.toFile());
		if (exists(outputFolder)) {
			throw new IOException("Unable to delete folder from disk for test: " + outputFolder.toAbsolutePath());
		}
	}

	private static FakeIndexControl control;

	@Before
	public void setUpForTest() throws IOException {
		control = new FakeIndexControl(CONFIG);
	}

	@Test
	public void shouldInitializeWorldWhenAskingForSchema() {
		final HbaseIndexSchema schema = control.getSchema();
		final Collection<String> documentTypes = schema.getDocumentTypes();
		assertTrue(documentTypes.contains("Email"));
	}

	@Test
	public void shouldInitializeWorldWhenAskingForIndex() throws InterruptedException {
		final HbaseIndex index = control.getIndex();
		final Entities samples = index.getSamples("Localname", 1);
		assertNotNull(samples);
	}

	@AfterClass
	public static void tearDownFolder() {
		delete(get(STORAGE_ROOT).toFile());
	}

	private static final String STORAGE_ROOT = "fake_index_control_test";
	private static final HbaseIndexingServiceConfiguration CONFIG = new HbaseIndexingServiceConfiguration();
	static {
		CONFIG.setRandomSeed(1234L);
		CONFIG.setPopulationCount(10);
		CONFIG.setAverageRelationCount(5);
		CONFIG.setAverageCommunicationCount(20);
		CONFIG.setStorageRootPath(STORAGE_ROOT);
	}
}