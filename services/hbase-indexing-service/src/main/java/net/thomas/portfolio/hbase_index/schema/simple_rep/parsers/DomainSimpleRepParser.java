package net.thomas.portfolio.hbase_index.schema.simple_rep.parsers;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.hbase_index.schema.util.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DomainSimpleRepParser extends SimpleRepresentationParserImpl {

	public static DomainSimpleRepParser newDomainParser(IdCalculator idCalculator) {
		return new DomainSimpleRepParser(idCalculator);
	}

	private DomainSimpleRepParser(IdCalculator idCalculator) {
		super("Domain", "\\w*(\\.\\w+)+$", idCalculator);
	}

	@Override
	protected void populateValues(DataType entity, String simpleRepresentation) {
		if (simpleRepresentation.charAt(0) == '.') {
			simpleRepresentation = simpleRepresentation.substring(1);
		}
		if (simpleRepresentation.contains(".")) {
			final int firstDot = simpleRepresentation.indexOf('.');
			entity.put("domainPart", simpleRepresentation.substring(0, firstDot));
			entity.put("domain", library.parse("Domain", simpleRepresentation.substring(firstDot)));
		} else {
			entity.put("domainPart", simpleRepresentation);
		}
	}

	@Override
	public String toString() {
		return asString(this);
	}
}