package net.thomas.portfolio.hbase_index.schema.visitor.utils;

import net.thomas.portfolio.hbase_index.schema.visitor.EntityVisitor;

public interface VisitorTester {
	String getName();

	EntityVisitor<InvocationCountingContext> getVisitor();
}