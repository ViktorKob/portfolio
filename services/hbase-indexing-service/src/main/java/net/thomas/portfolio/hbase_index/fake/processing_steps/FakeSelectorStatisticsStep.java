package net.thomas.portfolio.hbase_index.fake.processing_steps;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.world.ProcessingStep;
import net.thomas.portfolio.hbase_index.fake.world.World;
import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.documents.Conversation;
import net.thomas.portfolio.hbase_index.schema.documents.Email;
import net.thomas.portfolio.hbase_index.schema.documents.Event;
import net.thomas.portfolio.hbase_index.schema.documents.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.processed_data.SelectorStatistics;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.EntityHierarchyVisitorBuilder;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.EventContext;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityHierarchyVisitor.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.VisitorEntityPostActionFactory;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class FakeSelectorStatisticsStep implements ProcessingStep {
	@Override
	public void executeAndUpdateIndex(World world, HbaseIndex partiallyConstructedIndex) {
		((FakeHbaseIndex) partiallyConstructedIndex).setSelectorStatistics(generateSelectorStatistics(world.getEvents()));
	}

	private SelectorStatistics generateSelectorStatistics(Collection<? extends Event> events) {
		final SelectorStatistics statistics = new SelectorStatistics();
		final EntityHierarchyVisitor<EventContext> counter = buildCounter(statistics);
		for (final Event event : events) {
			counter.visit(event, new EventContext(event));
		}
		return statistics;
	}

	private EntityHierarchyVisitor<EventContext> buildCounter(final SelectorStatistics statistics) {
		return new EntityHierarchyVisitorBuilder<EventContext>().setEntityPostActionFactory(createActionFactory(statistics))
			.build();
	}

	private VisitorEntityPostActionFactory<EventContext> createActionFactory(final SelectorStatistics statistics) {
		final Set<Class<? extends Entity>> blankActionEntities = new HashSet<>(
				asList(EmailEndpoint.class, CommunicationEndpoint.class, Email.class, TextMessage.class, Conversation.class));

		final VisitorEntityPostActionFactory<EventContext> actionFactory = new VisitorEntityPostActionFactory<EventContext>() {
			@Override
			public <T extends Entity> VisitorEntityPostAction<T, EventContext> getEntityPostAction(Class<T> entityClass) {
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