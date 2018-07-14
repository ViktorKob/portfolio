package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.thomas.portfolio.hbase_index.fake.generators.SelectorGenerator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class EmailAddressGenerator extends SelectorGenerator {

	private final List<DataType> localnames;
	private final List<DataType> domains;

	public EmailAddressGenerator(Collection<DataType> localnames, Collection<DataType> domains, HbaseIndexSchema schema, long randomSeed) {
		super("EmailAddress", schema, randomSeed);
		this.localnames = new ArrayList<>(localnames);
		this.domains = new ArrayList<>(domains);
	}

	@Override
	protected boolean keyShouldBeUnique() {
		return false;
	}

	@Override
	protected void populateValues(DataType sample) {
		sample.put("localname", localnames.get(random.nextInt(localnames.size())));
		sample.put("domain", domains.get(random.nextInt(domains.size())));
	}
}
