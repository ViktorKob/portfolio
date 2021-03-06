package net.thomas.portfolio.hbase_index.fake.processing_steps;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.FakeWorldStorage;
import net.thomas.portfolio.hbase_index.fake.generators.FakeWorldGenerator;
import net.thomas.portfolio.hbase_index.fake.world.storage.EventReader;
import net.thomas.portfolio.hbase_index.fake.world.storage.EventWriter;
import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.processing.SchemaIntrospection;
import net.thomas.portfolio.hbase_index.schema.processing.utils.SelectorExtractor;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class FakeInvertedIndexStepUnitTest {
	private HbaseIndexSchema schema;
	private FakeHbaseIndex index;
	private SelectorExtractor selectorExtractor;
	private EventReader events;

	@Before
	public void setUpForTest() {
		schema = new SchemaIntrospection().examine(Email.class, TextMessage.class, Conversation.class).describeSchema();
		selectorExtractor = new SelectorExtractor();
		events = new FakeWorldStorage();
		new FakeWorldGenerator(1234L, 5, 10, 10).generateAndWrite((EventWriter) events);
		index = new FakeHbaseIndex();
		index.setEventReader(events);
		index.addEntitiesAndChildren(events);
		final FakeInvertedIndexStep invertedIndexStep = new FakeInvertedIndexStep();
		invertedIndexStep.executeAndUpdateIndex(events, index);
	}

	@Test
	public void shouldContainIndexForWorld() {
		for (final Event event : events) {
			final Set<SelectorEntity> selectors = selectorExtractor.extract(event);
			for (final SelectorEntity selector : selectors) {
				assertTrue("Could not find matching event using " + selector + " with " + event, hasMatchingEvent(event, selector));
			}
		}
	}

	private boolean hasMatchingEvent(final Event event, final SelectorEntity selector) {
		final String selectorType = simpleName(selector);
		final Collection<Indexable> indexables = schema.getIndexables(selectorType);
		for (final Indexable indexable : indexables) {
			final boolean hasInfo = hasMatchingInfo(event, selector, indexable);
			if (hasInfo) {
				return true;
			}
		}
		return false;
	}

	private boolean hasMatchingInfo(final Event event, final SelectorEntity selector, final Indexable indexable) {
		final String selectorType = simpleName(selector);
		final DocumentInfos infos = index.invertedIndexLookup(new DataTypeId(selectorType, selector.uid), indexable);
		for (final DocumentInfo info : infos.getInfos()) {
			if (info.getId().uid.equals(event.uid)) {
				return true;
			}
		}
		return false;
	}

	private String simpleName(final SelectorEntity entity) {
		return entity.getClass().getSimpleName();
	}
}