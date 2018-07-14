package net.thomas.portfolio.hbase_index.service;

import static java.util.Arrays.asList;
import static net.thomas.portfolio.hbase_index.fake.FakeHbaseIndexSchemaFactory.buildSchema;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.FakeWorld;
import net.thomas.portfolio.hbase_index.fake.index_steps.FakeInvertedIndexStep;
import net.thomas.portfolio.hbase_index.fake.index_steps.FakeSelectorStatisticsStep;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.IndexControl;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.IndexStep;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.World;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.WorldControl;

@Component
@Scope("singleton")
public class FakeIndexControl implements IndexControl {

	private HbaseIndexSchema schema;
	private HbaseIndex index;

	private List<IndexStep> indexSteps;
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
			final WorldControl worldControl = new WorldControl();
			if (!worldControl.canImportWorld()) {
				buildAndExportWorld(worldControl, randomSeed);
			}
			setSchema(worldControl.importSchema());
			setIndexSteps(asList(new FakeInvertedIndexStep(), new FakeSelectorStatisticsStep()));
			final World world = worldControl.importWorld();
			index(world);
			initialized = true;
		}
	}

	private void buildAndExportWorld(final WorldControl worldControl, long randomSeed) {
		final HbaseIndexSchema schema = buildSchema();
		final World world = new FakeWorld(schema, randomSeed, 80, 10, 800);
		worldControl.exportWorld(schema, world);
	}

	@Override
	public void setSchema(HbaseIndexSchema schema) {
		this.schema = schema;
	}

	public void setIndexSteps(List<IndexStep> indexSteps) {
		this.indexSteps = indexSteps;
	}

	@Override
	public synchronized void index(World world) {
		final FakeHbaseIndex index = new FakeHbaseIndex();
		index.addEntitiesAndChildren(world.getEvents());
		index.setReferences(world.getSourceReferences());
		for (final IndexStep step : indexSteps) {
			step.executeAndUpdateIndex(schema, world, index);
		}
		this.index = index;
	}
}