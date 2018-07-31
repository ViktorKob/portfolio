package net.thomas.portfolio.hbase_index.schema.processing.visitor.contexts;

import net.thomas.portfolio.hbase_index.schema.events.Event;

public class PathContext extends EventContext {
	public String path;

	public PathContext(Event source) {
		super(source);
	}
}