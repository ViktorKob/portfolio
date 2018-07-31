package net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts.VisitingContext;

public class FieldActions<CONTEXT_TYPE extends VisitingContext> {
	public final VisitorGenericFieldPreAction<CONTEXT_TYPE> preField;
	public final VisitorGenericFieldPostAction<CONTEXT_TYPE> postField;
	public final VisitorGenericFieldSimpleAction<CONTEXT_TYPE> simpleField;

	public FieldActions(VisitorGenericFieldPreAction<CONTEXT_TYPE> preField, VisitorGenericFieldPostAction<CONTEXT_TYPE> postField,
			VisitorGenericFieldSimpleAction<CONTEXT_TYPE> simpleField) {
		this.preField = preField;
		this.postField = postField;
		this.simpleField = simpleField;
	}
}