package net.thomas.portfolio.hbase_index.schema.processing.utils;

import java.util.HashSet;
import java.util.Set;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.utils.SelectorExtractor.SelectorExtractionContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;

public class SelectorExtractor implements VisitorEntityPreActionFactory<SelectorExtractionContext> {
	private final StrictEntityHierarchyVisitor<SelectorExtractionContext> extractor;

	public SelectorExtractor() {
		extractor = new StrictEntityHierarchyVisitorBuilder<SelectorExtractionContext>().setEntityPreActionFactory(this)
			.build();
	}

	@Override
	public <T extends Entity> VisitorEntityPreAction<T, SelectorExtractionContext> getEntityPreAction(Class<T> entityClass) {
		if (SelectorEntity.class.isAssignableFrom(entityClass)) {
			return (entity, context) -> {
				context.selectors.add((SelectorEntity) entity);
			};
		} else {
			return (entity, context) -> {
			};
		}
	}

	public Set<SelectorEntity> extract(Event event) {
		final SelectorExtractionContext context = new SelectorExtractionContext();
		extractor.visit(event, context);
		return context.selectors;
	}

	public static class SelectorExtractionContext implements VisitingContext {
		public final Set<SelectorEntity> selectors;

		public SelectorExtractionContext() {
			selectors = new HashSet<>();
		}
	}
}
