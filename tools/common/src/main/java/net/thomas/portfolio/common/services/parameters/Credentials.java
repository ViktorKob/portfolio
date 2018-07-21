package net.thomas.portfolio.common.services.parameters;

import java.util.Base64;

public class Credentials {
	private String user;
	private String password;
	private String encodedCredentials;

	public Credentials() {
		encodedCredentials = null;
	}

	public Credentials(String user, String password) {
		this.user = user;
		this.password = password;
		encodedCredentials = null;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getEncoded() {
		if (encodedCredentials == null) {
			final String plainCreds = user + ":" + password;
			final byte[] plainCredsBytes = plainCreds.getBytes();
			final byte[] base64CredsBytes = Base64.getEncoder()
				.encode(plainCredsBytes);
			encodedCredentials = new String(base64CredsBytes);
		}
		return encodedCredentials;
	}
}