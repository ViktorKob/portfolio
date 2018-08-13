package net.thomas.portfolio.common.services.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

public class ServiceDependencyUnitTest {
	private ServiceDependency dependency;

	@Before
	public void setUpForTest() {
		dependency = new ServiceDependency();
	}

	@Test
	public void shouldContainNameAfterSettingIt() {
		dependency.setName(SOME_NAME);
		assertEquals(SOME_NAME, dependency.getName());
	}

	@Test
	public void shouldContainCredentialsAfterSettingIt() {
		dependency.setCredentials(SOME_CREDENTIALS);
		assertSame(SOME_CREDENTIALS, dependency.getCredentials());
	}

	@Test
	public void shouldContainBothWhenUsingExplicitConstructor() {
		dependency = new ServiceDependency(SOME_NAME, SOME_CREDENTIALS);
		assertEquals(SOME_NAME, dependency.getName());
		assertSame(SOME_CREDENTIALS, dependency.getCredentials());
	}

	private static final String SOME_NAME = "SomeName";
	private static final Credentials SOME_CREDENTIALS = new Credentials("user", "password");
}