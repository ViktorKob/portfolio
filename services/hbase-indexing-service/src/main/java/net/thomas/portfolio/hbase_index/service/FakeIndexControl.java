package net.thomas.portfolio.hbase_index.service;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.FakeWorld;
import net.thomas.portfolio.hbase_index.fake.processing_steps.FakeInvertedIndexStep;
import net.thomas.portfolio.hbase_index.fake.processing_steps.FakeSelectorStatisticsStep;
import net.thomas.portfolio.hbase_index.fake.world.IndexControl;
import net.thomas.portfolio.hbase_index.fake.world.ProcessingStep;
import net.thomas.portfolio.hbase_index.fake.world.World;
import net.thomas.portfolio.hbase_index.fake.world.WorldIoControl;
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

	private List<ProcessingStep> indexSteps;
	private boolean initialized;
	private final long randomSeed;

	@Autowired
	public FakeIndexControl(HbaseIndexingServiceConfiguration config) {
		randomSeed = config.getRandomSeed();
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
			final WorldIoControl worldControl = new WorldIoControl();
			if (!worldControl.canImportWorld()) {
				buildAndExportWorld(worldControl, randomSeed);
			}
			setIndexSteps(asList(new FakeInvertedIndexStep(), new FakeSelectorStatisticsStep()));
			final World world = worldControl.importWorld();
			index(world);
			schema = new SchemaIntrospection().examine(Email.class)
				.examine(TextMessage.class)
				.examine(Conversation.class)
				.describeSchema();
			initialized = true;
		}
	}

	private void buildAndExportWorld(final WorldIoControl worldControl, long randomSeed) {
		final World world = new FakeWorld(randomSeed, 80, 10, 800);
		worldControl.exportWorld(world);
	}

	@Override
	public void setSchema(HbaseIndexSchema schema) {
		this.schema = schema;
	}

	public void setIndexSteps(List<ProcessingStep> indexSteps) {
		this.indexSteps = indexSteps;
	}

	@Override
	public synchronized void index(World world) {
		final FakeHbaseIndex index = new FakeHbaseIndex();
		index.addEntitiesAndChildren(world.getEvents());
		index.setReferences(world.getSourceReferences());
		for (final ProcessingStep step : indexSteps) {
			step.executeAndUpdateIndex(world, index);
		}
		this.index = index;
	}
}