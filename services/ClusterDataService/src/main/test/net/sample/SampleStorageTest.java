package net.sample;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.model.DataType;
import net.sample.SampleStorage;

public class SampleStorageTest {

	private SampleStorage storage;

	@Before
	public void setupSampler() {
		storage = new SampleStorage();
	}

	@Test
	public void shouldNotFailLookupOfNonExistantType() {
		storage.getDataType("Dummy", "X");
	}

	@Test
	public void shouldNotFailLookupOfNonExistantUid() {
		final DataType sample = new DataType("Localname");
		sample.setUid("X");
		storage.addDataType(sample);
		storage.getDataType("Localname", "Y");
	}

	@Test
	public void shouldRetreiveSample() {
		final DataType sample = new DataType("Localname");
		sample.setUid("X");
		storage.addDataType(sample);
		final DataType storedSample = storage.getDataType("Localname", "X");
		assertEquals(sample.getUid(), storedSample.getUid());
	}
}
