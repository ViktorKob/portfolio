package net.sample.generators.primitives;

import java.util.Random;

import net.model.meta_data.RecognitionLevel;

public class RecognitionLevelGenerator {
	private final Random random;

	public RecognitionLevelGenerator(long randomSeed) {
		random = new Random(randomSeed);
	}

	public RecognitionLevel generate() {
		return RecognitionLevel.values()[random.nextInt(RecognitionLevel.values().length)];
	}
}
