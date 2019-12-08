package net.thomas.portfolio.common.utils;

public class ProgrammingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProgrammingException(String message) {
		super(message);
	}

	public ProgrammingException(String message, Throwable cause) {
		super(message, cause);
	}
}
