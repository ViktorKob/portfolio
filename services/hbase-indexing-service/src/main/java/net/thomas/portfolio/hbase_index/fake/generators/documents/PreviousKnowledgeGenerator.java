package net.thomas.portfolio.hbase_index.fake.generators.documents;

import java.util.Random;

import net.thomas.portfolio.hbase_index.fake.generators.primitives.RecognitionLevelGenerator;
import net.thomas.portfolio.shared_objects.analytics.PreviousKnowledge;

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
