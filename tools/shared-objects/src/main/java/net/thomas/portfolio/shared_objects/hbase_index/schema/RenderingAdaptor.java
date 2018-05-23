package net.thomas.portfolio.shared_objects.hbase_index.schema;

public interface RenderingAdaptor {
	String renderAsSimpleRepresentation(String type, String uid);

	String renderAsText(String type, String uid);

	String renderAsHtml(String type, String uid);
}
