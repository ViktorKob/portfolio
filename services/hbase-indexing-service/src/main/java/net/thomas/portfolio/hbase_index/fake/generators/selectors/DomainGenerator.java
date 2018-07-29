package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.thomas.portfolio.hbase_index.fake.generators.SelectorGenerator;
import net.thomas.portfolio.hbase_index.fake.generators.primitives.StringGenerator;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;

public class DomainGenerator extends SelectorGenerator<Domain> {

	private final List<Domain> parentDomains;
	private final StringGenerator generator;

	public DomainGenerator(Collection<Domain> parentDomains, int minLength, int maxLength, long randomSeed) {
		super(randomSeed);
		this.parentDomains = new ArrayList<>(parentDomains);
		generator = new StringGenerator(minLength, maxLength, 0.0, randomSeed);
	}

	@Override
	protected Domain createInstance() {
		if (parentDomains.size() > 0) {
			return new Domain(generator.generate(), randomSample(parentDomains));
		} else {
			return new Domain(generator.generate(), null);
		}
	}
}