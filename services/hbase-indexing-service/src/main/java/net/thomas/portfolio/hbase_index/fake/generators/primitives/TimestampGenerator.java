package net.thomas.portfolio.hbase_index.fake.generators.primitives;

import java.util.Date;
import java.util.Random;

public class TimestampGenerator {
	private final long lowerBound;
	private final long upperBound;
	private final Random random;

	public TimestampGenerator(Date lowerBound, Date upperBound, long randomSeed) {
		this.lowerBound = lowerBound.getTime();
		this.upperBound = upperBound.getTime();
		random = new Random(randomSeed);
	}

	public long generate() {
		final long sampleSpace = upperBound - lowerBound;
		final long sample = (long) (sampleSpace * random.nextDouble());
		return sample + lowerBound;
	}
}
