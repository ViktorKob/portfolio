package net.thomas.portfolio.hbase_index.fake.processing_steps;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.events.ProcessingStep;
import net.thomas.portfolio.hbase_index.fake.world.storage.EventReader;
import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.processing.data.SelectorStatistics;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.EventContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class FakeSelectorStatisticsStep implements ProcessingStep {
	private static final Logger LOG = getLogger(FakeSelectorStatisticsStep.class);

	@Override
	public void executeAndUpdateIndex(final EventReader events, final HbaseIndex partiallyConstructedIndex) {
		((FakeHbaseIndex) partiallyConstructedIndex).setSelectorStatistics(generateSelectorStatistics(events));
	}

	private SelectorStatistics generateSelectorStatistics(final Iterable<Event> events) {
		final SelectorStatistics statistics = new SelectorStatistics();
		final StrictEntityHierarchyVisitor<EventContext> counter = buildCounter(statistics);
		LOG.info("Starting selector statistics step");
		final long stamp = currentTimeMillis();
		long eventCount = 0;
		for (final Event event : events) {
			counter.visit(event, new EventContext(event));
			eventCount++;
		}
		LOG.info("Seconds spend building selector statistics for " + eventCount + " events: " + (currentTimeMillis() - stamp) / 1000);
		return statistics;
	}

	private StrictEntityHierarchyVisitor<EventContext> buildCounter(final SelectorStatistics statistics) {
		return new StrictEntityHierarchyVisitorBuilder<EventContext>().setEntityPostActionFactory(createActionFactory(statistics)).build();
	}

	private VisitorEntityPostActionFactory<EventContext> createActionFactory(final SelectorStatistics statistics) {
		final Set<Class<? extends Entity>> blankActionEntities = new HashSet<>(
				asList(EmailEndpoint.class, CommunicationEndpoint.class, Email.class, TextMessage.class, Conversation.class));

		final VisitorEntityPostActionFactory<EventContext> actionFactory = new VisitorEntityPostActionFactory<EventContext>() {
			@Override
			public <T extends Entity> VisitorEntityPostAction<T, EventContext> getEntityPostAction(final Class<T> entityClass) {
				if (blankActionEntities.contains(entityClass)) {
					return (entity, context) -> {
					};
				} else {
					return (entity, context) -> {
						statistics.updateCounts((SelectorEntity) entity, context.source);
					};
				}
			}
		};
		return actionFactory;
	}

}