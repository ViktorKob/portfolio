package net.thomas.portfolio.hbase_index.fake.generators.primitives;

import java.util.Random;

public class StringGenerator {
	private final int minLength;
	private final int maxLength;
	private final double whiteSpacePropability;
	private final Random random;

	public StringGenerator(int minLength, int maxLength, double whiteSpacePropability, long randomSeed) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.whiteSpacePropability = whiteSpacePropability;
		random = new Random(randomSeed);
	}

	public String generate() {
		final int targetLength = random.nextInt(maxLength - minLength + 1) + minLength;
		final StringBuffer buffer = new StringBuffer();
		while (buffer.length() < targetLength) {
			if (shouldAddWhitespace(buffer)) {
				buffer.append(' ');
			} else {
				buffer.append((char) (random.nextInt('z' - 'a') + 'a'));
			}
		}
		return buffer.toString().trim();
	}

	private boolean shouldAddWhitespace(final StringBuffer buffer) {
		return buffer.length() > 0 && buffer.charAt(buffer.length() - 1) != ' ' && random.nextDouble() < whiteSpacePropability;
	}
}
