package net.thomas.portfolio.shared_objects.adaptors;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public interface RenderingAdaptor {
	String renderAsSimpleRepresentation(Selector selector);

	String renderAsText(DataType entity);

	String renderAsText(DataTypeId id);

	String renderAsHtml(DataType entity);

	String renderAsHtml(DataTypeId id);
}
