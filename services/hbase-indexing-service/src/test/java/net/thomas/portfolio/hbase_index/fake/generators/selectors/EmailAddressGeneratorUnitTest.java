package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;

public class EmailAddressGeneratorUnitTest {
	private static final Localname SOME_LOCALNAME = new Localname("someLocalname");
	private static final Domain SOME_DOMAIN = new Domain("some.domain", null);
	private static final long SOME_RANDOM_SEED = 1l;
	private static Iterator<EmailAddress> GENERATOR;

	@BeforeClass
	public static void setupGenerator() {
		SOME_LOCALNAME.uid = "AA";
		SOME_DOMAIN.uid = "BB";
		GENERATOR = new EmailAddressGenerator(singleton(SOME_LOCALNAME), singleton(SOME_DOMAIN), SOME_RANDOM_SEED).iterator();
	}

	@Test
	public void shouldHaveLocalname() {
		final EmailAddress localname = GENERATOR.next();
		assertNotNull(localname.localname);
	}

	@Test
	public void shouldHaveDomain() {
		final EmailAddress emailAddress = GENERATOR.next();
		assertNotNull(emailAddress.domain);
	}
}