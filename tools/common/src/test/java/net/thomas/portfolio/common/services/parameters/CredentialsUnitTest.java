package net.thomas.portfolio.common.services.parameters;

import static java.util.Base64.getEncoder;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CredentialsUnitTest {
	private static final String SOME_NAME = "Some name";
	private static final String SOME_PASSWORD = "Some password";
	private Credentials credentials;

	@Before
	public void setUpForTest() {
		credentials = new Credentials(SOME_NAME, SOME_PASSWORD);
	}

	@Test
	public void shouldContainName() {
		assertEquals(SOME_NAME, credentials.getUser());
	}

	@Test
	public void shouldContainPassword() {
		assertEquals(SOME_PASSWORD, credentials.getPassword());
	}

	@Test
	public void shouldContainNameFromSetter() {
		credentials = new Credentials();
		credentials.setUser(SOME_NAME);
		assertEquals(SOME_NAME, credentials.getUser());
	}

	@Test
	public void shouldContainPasswordFromSetter() {
		credentials = new Credentials();
		credentials.setPassword(SOME_PASSWORD);
		assertEquals(SOME_PASSWORD, credentials.getPassword());
	}

	@Test
	public void shouldCorrectlyBase64EncodeCredentials() {
		final String expectedCredentials = new String(getEncoder().encode((SOME_NAME + ":" + SOME_PASSWORD).getBytes()));
		assertEquals(expectedCredentials, credentials.getEncoded());
	}
}
