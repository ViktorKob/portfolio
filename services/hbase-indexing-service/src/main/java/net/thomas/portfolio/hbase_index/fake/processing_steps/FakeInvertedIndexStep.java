package net.thomas.portfolio.hbase_index.fake.processing_steps;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.world.ProcessingStep;
import net.thomas.portfolio.hbase_index.fake.world.World;
import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.annotations.IndexablePath;
import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.processing.data.InvertedIndex;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.PathContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

public class FakeInvertedIndexStep implements ProcessingStep {
	@Override
	public void executeAndUpdateIndex(World world, HbaseIndex partiallyConstructedIndex) {
		((FakeHbaseIndex) partiallyConstructedIndex).setInvertedIndex(generateInvertedIndex(world.getEvents()));
	}

	private InvertedIndex generateInvertedIndex(Collection<? extends Event> events) {
		final InvertedIndex invertedIndex = new InvertedIndex();
		final StrictEntityHierarchyVisitor<PathContext> traversal = buildIndexer(invertedIndex);
		indexEvents(events, traversal);
		return invertedIndex;
	}

	private StrictEntityHierarchyVisitor<PathContext> buildIndexer(final InvertedIndex invertedIndex) {
		return new StrictEntityHierarchyVisitorBuilder<PathContext>().setEntityPostActionFactory(createEntityPostActionFactory(invertedIndex))
			.setFieldPreActionFactory(createFieldPreActionFactory(invertedIndex))
			.build();
	}

	private VisitorEntityPostActionFactory<PathContext> createEntityPostActionFactory(final InvertedIndex invertedIndex) {
		final Set<Class<? extends Entity>> blankActionEntities = new HashSet<>(
				asList(EmailEndpoint.class, CommunicationEndpoint.class, Email.class, TextMessage.class, Conversation.class));

		final VisitorEntityPostActionFactory<PathContext> actionFactory = new VisitorEntityPostActionFactory<PathContext>() {
			@Override
			public <T extends Entity> VisitorEntityPostAction<T, PathContext> getEntityPostAction(Class<T> entityClass) {
				if (blankActionEntities.contains(entityClass)) {
					return (entity, context) -> {
					};
				} else {
					return (entity, context) -> {
						invertedIndex.add((SelectorEntity) entity, context.source, context.path);
					};
				}
			}
		};
		return actionFactory;
	}

	private void indexEvents(Collection<? extends Event> events, final StrictEntityHierarchyVisitor<PathContext> traversal) {
		for (final Event event : events) {
			traversal.visit(event, new PathContext(event));
		}
	}

	private VisitorFieldPreActionFactory<PathContext> createFieldPreActionFactory(final InvertedIndex invertedIndex) {
		final Map<Class<? extends Entity>, Map<String, String>> pathMappings = new HashMap<>();
		for (final Class<? extends Entity> entityClass : new HashSet<>(asList(Email.class, TextMessage.class, Conversation.class))) {
			pathMappings.put(entityClass, new HashMap<>());
			for (final Field field : entityClass.getFields()) {
				if (field.getAnnotation(IndexablePath.class) != null) {
					final String annotatedPath = field.getAnnotation(IndexablePath.class)
						.value();
					pathMappings.get(entityClass)
						.put(field.getName(), annotatedPath);
				}
			}
		}

		return new VisitorFieldPreActionFactory<PathContext>() {
			// TODO[Thomas]: Consider a path stack solution instead
			@Override
			public <T extends Entity> VisitorFieldPreAction<T, PathContext> getFieldPreAction(Class<T> entityClass, String field) {
				if (pathMappings.containsKey(entityClass) && pathMappings.get(entityClass)
					.containsKey(field)) {
					final String path = pathMappings.get(entityClass)
						.get(field);
					return (entity, context) -> {
						context.path = path;
					};
				} else {
					return (entity, context) -> {
					};
				}
			}
		};

	}
}