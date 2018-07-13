package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParser;

public class DomainSimpleRepParser extends SimpleRepresentationParser {

	public static DomainSimpleRepParser newDomainParser(IdCalculator idCalculator) {
		return new DomainSimpleRepParser(idCalculator);
	}

	private DomainSimpleRepParser(IdCalculator idCalculator) {
		super("Domain", "\\w*(\\.\\w+)+$", idCalculator);
	}

	@Override
	protected void populateValues(DataType entity, String source) {
		if (source.charAt(0) == '.') {
			source = source.substring(1);
		}
		if (source.contains(".")) {
			final int firstDot = source.indexOf('.');
			entity.put("domainPart", source.substring(0, firstDot));
			entity.put("domain", library.parse("Domain", source.substring(firstDot)));
		} else {
			entity.put("domainPart", source);
		}
	}

	@Override
	public String getImplementationClass() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return asString(this);
	}
}