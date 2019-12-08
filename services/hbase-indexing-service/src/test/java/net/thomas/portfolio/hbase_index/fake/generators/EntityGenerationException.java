package net.thomas.portfolio.hbase_index.fake.generators;

public class EntityGenerationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EntityGenerationException(String message) {
		super(message);
	}

	public EntityGenerationException(String message, Throwable cause) {
		super(message, cause);
	}
}