package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class DomainGenerator extends NameGenerator {

	private final List<DataType> parentDomains;

	public DomainGenerator(Collection<DataType> parentDomains, int minLength, int maxLength, boolean allowWhitespace, HbaseIndexSchema schema,
			long randomSeed) {
		super("Domain", "domainPart", minLength, maxLength, 0.0, schema, randomSeed);
		this.parentDomains = new ArrayList<>(parentDomains);
	}

	@Override
	protected boolean keyShouldBeUnique() {
		return false;
	}

	@Override
	protected void populateValues(DataType sample) {
		super.populateValues(sample);
		if (parentDomains.size() > 0) {
			sample.put("domain", parentDomains.get(random.nextInt(parentDomains.size())));
		}
	}
}
