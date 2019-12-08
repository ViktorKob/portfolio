package net.thomas.portfolio.service_commons.network;

public class UnableToCompleteRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnableToCompleteRequestException(String message) {
		super(message);
	}

	public UnableToCompleteRequestException(String message, Exception cause) {
		super(message, cause);
	}
}