package net.sample;

import net.model.DataType;

public class SampleDataSetGeneratorTester {
	public static void main(String[] args) {
		final SampleStorage samples = SampleDataSetGenerator.getSampleDataSet();
		final DataType address = samples.getDataType("EmailAddress", "D3F32B3ECA3717D7ED3F82BF42ABEB09");
		System.out.println(samples.getStatistics(address));
	}
}
