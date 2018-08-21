package net.thomas.portfolio.hbase_index.service;

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
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

@Component
@Scope("singleton")
public class FakeIndexControl implements IndexControl {
	private HbaseIndexSchema schema;
	private HbaseIndex index;

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

	private synchronized void initialize() {
		if (!initialized) {
			final EventDiskReader events = new EventDiskReader(storageRootPath);
			index = startBuildingIndex(events);
			schema = new SchemaIntrospection().examine(Email.class, TextMessage.class, Conversation.class).describeSchema();
			initialized = true;
		}
	}

	private FakeHbaseIndex startBuildingIndex(final EventDiskReader events) {
		final FakeHbaseIndex index = new FakeHbaseIndex();
		index.setEventReader(events);
		new Thread(() -> {
			processEventsIntoIndex(events, index);
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