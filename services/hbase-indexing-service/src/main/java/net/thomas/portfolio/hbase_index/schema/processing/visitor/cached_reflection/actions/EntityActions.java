package net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions;

import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

public class EntityActions<CONTEXT_TYPE extends VisitingContext> {
	public final VisitorGenericEntityPreAction<CONTEXT_TYPE> preEntity;
	public final VisitorGenericEntityPostAction<CONTEXT_TYPE> postEntity;
	public final Map<String, FieldActions<CONTEXT_TYPE>> fieldActions;

	public EntityActions(VisitorGenericEntityPreAction<CONTEXT_TYPE> preEntity, VisitorGenericEntityPostAction<CONTEXT_TYPE> postEntity,
			Map<String, FieldActions<CONTEXT_TYPE>> fieldActions) {
		this.preEntity = preEntity;
		this.postEntity = postEntity;
		this.fieldActions = fieldActions;
	}

}