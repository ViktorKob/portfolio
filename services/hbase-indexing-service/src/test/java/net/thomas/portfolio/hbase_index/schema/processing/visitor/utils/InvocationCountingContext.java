package net.thomas.portfolio.hbase_index.schema.processing.visitor.utils;

import java.util.HashMap;
import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

public class InvocationCountingContext implements VisitingContext {
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