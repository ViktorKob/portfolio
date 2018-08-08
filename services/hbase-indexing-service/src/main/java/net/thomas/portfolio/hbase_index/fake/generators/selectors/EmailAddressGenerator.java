package net.thomas.portfolio.hbase_index.fake.generators.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.thomas.portfolio.hbase_index.fake.generators.SelectorGenerator;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;

public class EmailAddressGenerator extends SelectorGenerator<EmailAddress> {

	private final List<Localname> localnames;
	private final List<Domain> domains;

	public EmailAddressGenerator(Collection<Localname> localnames, Collection<Domain> domains, long randomSeed) {
		super(randomSeed);
		this.localnames = new ArrayList<>(localnames);
		this.domains = new ArrayList<>(domains);
	}

	@Override
	protected EmailAddress createInstance() {
		return new EmailAddress(randomSample(localnames), randomSample(domains));
	}
}