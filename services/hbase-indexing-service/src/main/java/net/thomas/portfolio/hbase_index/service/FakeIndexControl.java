package net.thomas.portfolio.hbase_index.service;

import static java.lang.System.currentTimeMillis;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.events.IndexControl;
import net.thomas.portfolio.hbase_index.fake.generators.FakeWorldGenerator;
import net.thomas.portfolio.hbase_index.fake.processing_steps.FakeInvertedIndexStep;
import net.thomas.portfolio.hbase_index.fake.processing_steps.FakeSelectorStatisticsStep;
import net.thomas.portfolio.hbase_index.fake.world.storage.EventDiskReader;
import net.thomas.portfolio.hbase_index.fake.world.storage.EventDiskWriter;
import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.processing.SchemaIntrospection;
import net.thomas.portfolio.hbase_index.schema.simple_rep.SimpleRepresentationParserLibrary;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

@Component
@Scope("singleton")
public class FakeIndexControl implements IndexControl {
	private static final Logger LOG = getLogger(FakeIndexControl.class);

	private HbaseIndexSchema schema;
	private HbaseIndex index;
	private SimpleRepresentationParserLibrary parserLibrary;

	private boolean initialized;
	private final long randomSeed;
	private final int populationCount;
	private final int averageRelationCount;
	private final int averageCommunicationCount;
	private final String storageRootPath;

	@Autowired
	public FakeIndexControl(final HbaseIndexingServiceConfiguration config) {
		randomSeed = config.getRandomSeed();
		populationCount = config.getPopulationCount();
		averageRelationCount = config.getAverageRelationCount();
		averageCommunicationCount = config.getAverageCommunicationCount();
		storageRootPath = config.getStorageRootPath();
		initialized = false;
	}

	@Override
	@Bean
	public HbaseIndexSchema getSchema() {
		initialize();
		return schema;
	}

	@Override
	@Bean
	public HbaseIndex getIndex() {
		initialize();
		return index;
	}

	@Override
	@Bean
	public SimpleRepresentationParserLibrary getSimpleRepresentationParserLibrary() {
		initialize();
		return parserLibrary;
	}

	private synchronized void initialize() {
		if (!initialized) {
			final EventDiskReader events = new EventDiskReader(storageRootPath);
			index = startBuildingIndex(events);
			final SchemaIntrospection introspection = new SchemaIntrospection().examine(Email.class, TextMessage.class, Conversation.class);
			schema = introspection.describeSchema();
			parserLibrary = introspection.describeSimpleRepresentationParsers();
			initialized = true;
		}
	}

	private FakeHbaseIndex startBuildingIndex(final EventDiskReader events) {
		final FakeHbaseIndex index = new FakeHbaseIndex();
		index.setEventReader(events);
		new Thread(() -> {
			LOG.info("Initializing data for service...");
			final long stamp = currentTimeMillis();
			processEventsIntoIndex(events, index);
			LOG.info("Done initializing data for service in " + (currentTimeMillis() - stamp) / 1000 + " seconds.");
		}).start();
		return index;
	}

	private void processEventsIntoIndex(final EventDiskReader events, final FakeHbaseIndex index) {
		if (!events.canRead()) {
			buildAndExportWorld(new EventDiskWriter(storageRootPath), randomSeed);
		}
		index.addEntitiesAndChildren(events);
		new FakeInvertedIndexStep().executeAndUpdateIndex(events, index);
		new FakeSelectorStatisticsStep().executeAndUpdateIndex(events, index);
	}

	private void buildAndExportWorld(final EventDiskWriter worldWriter, final long randomSeed) {
		final FakeWorldGenerator world = new FakeWorldGenerator(randomSeed, populationCount, averageRelationCount, averageCommunicationCount);
		world.generateAndWrite(worldWriter);
	}
}