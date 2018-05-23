package net.thomas.portfolio.shared_objects.hbase_index.schema;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public interface RenderingAdaptor {
	String renderAsSimpleRepresentation(Selector selector);

	String renderAsText(DataType entity);

	String renderAsHtml(DataType entity);
}
