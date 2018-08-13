package net.thomas.portfolio.service_commons.network;

public class UnauthorizedAccessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnauthorizedAccessException(String message, Exception e) {
		super(message, e);
	}
}
