package net.thomas.portfolio.hbase_index.fake.generators.primitives;

import java.util.Random;

import net.thomas.portfolio.shared_objects.analytics.RecognitionLevel;

public class RecognitionLevelGenerator {
	private final Random random;

	public RecognitionLevelGenerator(long randomSeed) {
		random = new Random(randomSeed);
	}

	public RecognitionLevel generate() {
		return RecognitionLevel.values()[random.nextInt(RecognitionLevel.values().length)];
	}
}
