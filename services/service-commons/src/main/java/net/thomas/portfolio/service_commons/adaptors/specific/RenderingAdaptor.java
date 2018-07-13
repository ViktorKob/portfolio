package net.thomas.portfolio.service_commons.adaptors.specific;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public interface RenderingAdaptor {
	/***
	 * @param selectorId
	 *            The ID of the selector to lookup a simple representation for
	 * @return The simple representation, if the selector is known in the index, null otherwise.
	 */
	String renderAsSimpleRepresentation(DataTypeId selectorId);

	/***
	 * @param id
	 *            The ID of the data type to render
	 * @return A textual representation of the type, if the data type exists, null otherwise
	 */
	String renderAsText(DataTypeId id);

	/***
	 * @param id
	 *            The ID of the data type to render
	 * @return An HTML representation of the type, if the data type exists, null otherwise
	 */
	String renderAsHtml(DataTypeId id);
}
