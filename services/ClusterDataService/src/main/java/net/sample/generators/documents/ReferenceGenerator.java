package net.sample.generators.documents;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.model.meta_data.Classification;
import net.model.meta_data.Reference;
import net.model.meta_data.Source;
import net.sample.generators.primitives.DigitsGenerator;
import net.sample.generators.primitives.StringGenerator;

public class ReferenceGenerator {
	private final Random random;
	private final StringGenerator stringGenerator;
	private final DigitsGenerator numberGenerator;

	public ReferenceGenerator(long randomSeed) {
		random = new Random(randomSeed);
		stringGenerator = new StringGenerator(8, 10, 0.0, random.nextLong());
		numberGenerator = new DigitsGenerator(1, 10, random.nextLong());
	}

	public Reference generate() {
		final Source source = Source.values()[random.nextInt(Source.values().length)];
		final int classificationCount = random.nextInt(3) + 1;
		final Set<Classification> classifications = new HashSet<>();
		while (classifications.size() < classificationCount) {
			classifications.add(Classification.values()[random.nextInt(Classification.values().length)]);
		}
		return new Reference(source, classifications, stringGenerator.generate() + "-" + numberGenerator.generate());
	}
}
