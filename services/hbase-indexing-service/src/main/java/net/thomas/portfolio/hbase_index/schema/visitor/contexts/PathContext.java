package net.thomas.portfolio.hbase_index.schema.visitor.contexts;

import net.thomas.portfolio.hbase_index.schema.documents.Event;

public class PathContext extends EventContext {
	public String path;

	public PathContext(Event source) {
		super(source);
	}
}