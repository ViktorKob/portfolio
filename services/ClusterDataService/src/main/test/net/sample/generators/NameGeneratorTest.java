package net.sample.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import net.model.DataType;
import net.sample.generators.selectors.NameGenerator;

public class NameGeneratorTest {

	private Iterator<DataType> generator;

	@Before
	public void setupSampler() {
		generator = new NameGenerator("Localname", "name", 3, 15, 0.2, 1234l);
	}

	@Test
	public void shouldGenerateSampleLocalname() {
		final DataType localname = generator.next();
		assertEquals("Localname", localname.getType());
	}

	@Test
	public void shouldGenerateDifferentLocalnames() {
		final DataType localname1 = generator.next();
		final DataType localname2 = generator.next();
		assertFalse(localname1.get("name").equals(localname2.get("name")));
	}
}