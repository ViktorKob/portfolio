package net.sample.generators.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.model.DataType;
import net.sample.generators.SelectorGenerator;

public class EmailAddressGenerator extends SelectorGenerator {

	private final List<DataType> localnames;
	private final List<DataType> domains;

	public EmailAddressGenerator(Collection<DataType> localnames, Collection<DataType> domains, long randomSeed) {
		super("EmailAddress", randomSeed);
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
