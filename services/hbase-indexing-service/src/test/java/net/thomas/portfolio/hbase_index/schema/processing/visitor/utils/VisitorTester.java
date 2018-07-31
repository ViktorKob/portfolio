package net.thomas.portfolio.hbase_index.schema.processing.visitor.utils;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.EntityVisitor;

public interface VisitorTester {
	String getName();

	EntityVisitor<InvocationCountingContext> getVisitor();
}