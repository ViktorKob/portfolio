package net.thomas.portfolio.hbase_index.schema.processing.visitor;

public class EntityTraversalException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EntityTraversalException(String message) {
		super(message);
	}

	public EntityTraversalException(String message, Throwable cause) {
		super(message, cause);
	}
}