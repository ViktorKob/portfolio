package net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts;

import net.thomas.portfolio.hbase_index.schema.events.Event;

public class EventContext implements VisitingContext {
	public final Event source;

	public EventContext(Event source) {
		this.source = source;
	}
}