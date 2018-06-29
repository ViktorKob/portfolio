package net.thomas.portfolio.hbase_index.fake;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.IndexControl;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.IndexStep;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.World;

@Component
@Scope("singleton")
public class FakeIndexControl implements IndexControl {

	private HbaseIndexSchema schema;
	private HbaseIndex index;

	private List<IndexStep> indexSteps;

	public FakeIndexControl() {
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

	@Override
	public HbaseIndexSchema getSchema() {
		return schema;
	}

	@Override
	public synchronized HbaseIndex getIndex() {
		return index;
	}
}