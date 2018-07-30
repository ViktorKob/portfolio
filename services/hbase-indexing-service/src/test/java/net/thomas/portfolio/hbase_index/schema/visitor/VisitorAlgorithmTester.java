package net.thomas.portfolio.hbase_index.schema.visitor;

import java.util.HashMap;
import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.visitor.VisitorAlgorithmTester.InvocationCountingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;

public class VisitorAlgorithmTester
		implements VisitorFieldSimpleActionFactory<InvocationCountingContext>, VisitorEntityPreActionFactory<InvocationCountingContext>,
		VisitorEntityPostActionFactory<InvocationCountingContext>, VisitorFieldPreActionFactory<InvocationCountingContext>,
		VisitorFieldPostActionFactory<InvocationCountingContext>, VisitorGenericFieldSimpleActionFactory<InvocationCountingContext>,
		VisitorGenericEntityPreActionFactory<InvocationCountingContext>, VisitorGenericEntityPostActionFactory<InvocationCountingContext>,
		VisitorGenericFieldPreActionFactory<InvocationCountingContext>, VisitorGenericFieldPostActionFactory<InvocationCountingContext> {

	public static class InvocationCountingContext implements VisitingContext {
		private final Map<Entity, Map<String, Integer>> invokedActionCounts;

		public InvocationCountingContext() {
			invokedActionCounts = new HashMap<>();
		}

		public void addEntityAction(Entity entity, String action) {
			if (!invokedActionCounts.containsKey(entity)) {
				invokedActionCounts.put(entity, new HashMap<>());
			}
			final Map<String, Integer> actionMap = invokedActionCounts.get(entity);
			if (!actionMap.containsKey(action)) {
				actionMap.put(action, 0);
			}
			actionMap.put(action, actionMap.get(action) + 1);
		}

		public void addFieldAction(Entity entity, String action, String field) {
			addEntityAction(entity, action + "-" + field);
		}

		public int getEntityActionCount(Entity entity, String action) {
			if (invokedActionCounts.containsKey(entity) && invokedActionCounts.get(entity)
				.containsKey(action)) {
				return invokedActionCounts.get(entity)
					.get(action);
			} else {
				return 0;
			}
		}

		public int getFieldActionCount(Entity entity, String action, String field) {
			return getEntityActionCount(entity, action + "-" + field);
		}
	}

	@Override
	public <T extends Entity> VisitorEntityPreAction<T, InvocationCountingContext> getEntityPreAction(Class<T> entityClass) {
		return (entity, context) -> {
			context.addEntityAction(entity, "entityPreAction");
		};
	}

	@Override
	public VisitorGenericEntityPreAction<InvocationCountingContext> getGenericEntityPreAction(Class<? extends Entity> entityClass) {
		return (entity, context) -> {
			context.addEntityAction(entity, "entityPreAction");
		};
	}

	@Override
	public <T extends Entity> VisitorEntityPostAction<T, InvocationCountingContext> getEntityPostAction(Class<T> entityClass) {
		return (entity, context) -> {
			context.addEntityAction(entity, "entityPostAction");
		};
	}

	@Override
	public VisitorGenericEntityPostAction<InvocationCountingContext> getGenericEntityPostAction(Class<? extends Entity> entityClass) {
		return (entity, context) -> {
			context.addEntityAction(entity, "entityPostAction");
		};
	}

	@Override
	public <T extends Entity> VisitorFieldPreAction<T, InvocationCountingContext> getFieldPreAction(Class<T> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, "fieldPreAction", field);
		};
	}

	@Override
	public VisitorGenericFieldPreAction<InvocationCountingContext> getGenericFieldPreAction(Class<? extends Entity> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, "fieldPreAction", field);
		};
	}

	@Override
	public <T extends Entity> VisitorFieldSimpleAction<T, InvocationCountingContext> getSimpleFieldAction(Class<T> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, "fieldSimpleAction", field);
		};
	}

	@Override
	public VisitorGenericFieldSimpleAction<InvocationCountingContext> getGenericSimpleFieldAction(Class<? extends Entity> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, "fieldSimpleAction", field);
		};
	}

	@Override
	public <T extends Entity> VisitorFieldPostAction<T, InvocationCountingContext> getFieldPostAction(Class<T> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, "fieldPostAction", field);
		};
	}

	@Override
	public VisitorGenericFieldPostAction<InvocationCountingContext> getGenericFieldPostAction(Class<? extends Entity> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, "fieldPostAction", field);
		};
	}
}