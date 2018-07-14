package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParser;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailAddressSimpleRepParser extends SimpleRepresentationParser {

	public static EmailAddressSimpleRepParser newEmailAddressParser(IdCalculator idCalculator) {
		return new EmailAddressSimpleRepParser(idCalculator);
	}

	private EmailAddressSimpleRepParser(IdCalculator idCalculator) {
		super("EmailAddress", "[\\w\\.]+@\\w+(\\.\\w+)+$", idCalculator);
	}

	@Override
	protected void populateValues(DataType entity, String simpleRepresenation) {
		final String[] parts = simpleRepresenation.split("@");
		entity.put("localname", library.parse("Localname", parts[0]));
		entity.put("domain", library.parse("Domain", parts[1]));
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