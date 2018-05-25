package net.thomas.portfolio.shared_objects.adaptors;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public interface RenderingAdaptor {
	String renderAsSimpleRepresentation(DataTypeId selectorId);

	String renderAsText(DataTypeId id);

	String renderAsHtml(DataTypeId id);
}
