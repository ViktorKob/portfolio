package net.sample.generators.documents;

import java.util.Random;

import net.model.meta_data.PreviousKnowledge;
import net.sample.generators.primitives.RecognitionLevelGenerator;

public class PreviousKnowledgeGenerator {
	private final Random random;
	private final RecognitionLevelGenerator recognitionGenerator;

	public PreviousKnowledgeGenerator(long randomSeed) {
		random = new Random(randomSeed);
		recognitionGenerator = new RecognitionLevelGenerator(random.nextLong());
	}

	public PreviousKnowledge generate() {
		return new PreviousKnowledge(recognitionGenerator.generate(), recognitionGenerator.generate());
	}
}
