package net.thomas.portfolio.service_commons.network;

public class BadRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BadRequestException(String message, Exception e) {
		super(message, e);
	}
}