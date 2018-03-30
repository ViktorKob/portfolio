package net.sample.generators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import net.model.DataType;
import net.sample.generators.primitives.StringGenerator;

public class DataTypeGeneratorTest {

	private Iterator<DataType> sampler;

	@Before
	public void setupSampler() {
		sampler = new FakeSampler().iterator();
	}

	@Test
	public void shouldGenerateUid() {
		final DataType localname = sampler.next();
		assertNotNull(localname.getUid());
	}

	@Test
	public void shouldGenerateValidUid() {
		final DataType localname = sampler.next();
		final String uid = localname.getUid();
		assertTrue(uid.length() == 32);
	}

	@Test
	public void shouldGenerateDifferentSampleKeys() {
		final DataType sample1 = sampler.next();
		final DataType sample2 = sampler.next();
		assertFalse(sample1.getUid().equals(sample2.getUid()));
	}

	private static class FakeSampler extends DataTypeGenerator {
		private final StringGenerator generator;

		public FakeSampler() {
			super("Localname", false, 1234l);
			generator = new StringGenerator(3, 15, 0.0, random.nextLong());
		}

		@Override
		protected boolean keyShouldBeUnique() {
			return false;
		}

		@Override
		protected void populateValues(DataType sample) {
			sample.put("name", generateLocalname());
		}

		private String generateLocalname() {
			return generator.generate();
		}
	}
}
