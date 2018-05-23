package net.thomas.portfolio.hbase_index.fake.generators.primitives;

import java.util.Random;

public class DigitsGenerator {
	private final int minLength;
	private final int maxLength;
	private final Random random;

	public DigitsGenerator(int minLength, int maxLength, long randomSeed) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		random = new Random(randomSeed);
	}

	public String generate() {
		int targetLength;
		if (minLength != maxLength) {
			targetLength = random.nextInt(maxLength - minLength + 1) + minLength;
		} else {
			targetLength = maxLength;
		}
		final StringBuffer buffer = new StringBuffer();
		while (buffer.length() < targetLength) {
			buffer.append(random.nextInt(10));
		}
		return buffer.toString().trim();
	}
}
