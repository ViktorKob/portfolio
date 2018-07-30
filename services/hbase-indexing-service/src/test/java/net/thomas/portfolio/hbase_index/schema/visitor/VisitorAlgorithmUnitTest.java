package net.thomas.portfolio.hbase_index.schema.visitor;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;
import net.thomas.portfolio.hbase_index.schema.visitor.utils.CachingVisitorTester;
import net.thomas.portfolio.hbase_index.schema.visitor.utils.InvocationCountingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.utils.NaiveVisitorTester;
import net.thomas.portfolio.hbase_index.schema.visitor.utils.StrictVisitorTester;
import net.thomas.portfolio.hbase_index.schema.visitor.utils.VisitorTester;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class VisitorAlgorithmUnitTest {

	protected static final int NEVER = 0;
	protected static final int ONCE = 1;
	protected static final List<VisitorTester> algorithms = asList(new NaiveVisitorTester(), new CachingVisitorTester(), new StrictVisitorTester());
	protected static InvocationCountingContext[] countingContexts;

	protected void visit(final Entity entity) {
		for (int i = 0; i < countingContexts.length; i++) {
			countingContexts[i] = new InvocationCountingContext();
			final EntityVisitor<InvocationCountingContext> algorithm = algorithms.get(i)
				.getVisitor();
			algorithm.visit(entity, countingContexts[i]);
		}
	}

	protected void assertEqualsForAllAlgorithms(String action, Entity entity, int occurrances) {
		for (int i = 0; i < algorithms.size(); i++) {
			final int actualCount = countingContexts[i].getEntityActionCount(entity, action);
			final String message = algorithms.get(0)
				.getName() + ": Count for " + action + " was wrong; should have been " + occurrances + ", but was " + actualCount + " for entity "
					+ entity.getClass()
						.getSimpleName();
			assertEquals(message, occurrances, actualCount);
		}
	}

	protected void assertThatAllAlgorithms(String action, Entity entity, String field, int occurrances) {
		for (int i = 0; i < algorithms.size(); i++) {
			final int actualCount = countingContexts[i].getFieldActionCount(entity, action, field);
			final String message = algorithms.get(0)
				.getName() + ": Count for " + action + ", field " + field + " was wrong; should have been " + occurrances + ", but was " + actualCount
					+ " for entity " + entity.getClass()
						.getSimpleName();
			assertEquals(message, occurrances, actualCount);
		}
	}

	public static final String INVOKED_ENTITY_PRE_ACTION_ON = "preEntityAction";
	public static final String INVOKED_ENTITY_POST_ACTION_ON = "postEntityAction";
	public static final String INVOKED_FIELD_PRE_ACTION_ON = "preFieldAction";
	public static final String INVOKED_FIELD_POST_ACTION_ON = "postFieldAction";
	public static final String INVOKED_FIELD_SIMPLE_ACTION_ON = "simpleFieldAction";

	public static final DisplayedName SOME_DISPLAYED_NAME = new DisplayedName("name");
	public static final Localname SOME_LOCALNAME = new Localname("name");
	public static final Domain SOME_TOP_LEVEL_DOMAIN = new Domain("part");
	public static final Domain SOME_DOMAIN = new Domain("part", SOME_TOP_LEVEL_DOMAIN);
	public static final EmailAddress SOME_EMAIL_ADDRESS = new EmailAddress(SOME_LOCALNAME, SOME_DOMAIN);
	public static final EmailEndpoint SOME_EMAIL_ENDPOINT = new EmailEndpoint(SOME_DISPLAYED_NAME, SOME_EMAIL_ADDRESS);
	public static final EmailEndpoint EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME = new EmailEndpoint(null, SOME_EMAIL_ADDRESS);
	public static final EmailEndpoint EMAIL_ENDPOINT_MISSING_ADDRESS = new EmailEndpoint(SOME_DISPLAYED_NAME, null);
	public static final Timestamp SOME_TIMESTAMP = new Timestamp(1l);
	public static final String SOME_STRING = "string";
	public static final String SOME_MESSAGE = "message";
	public static final Email SOME_EMAIL = new Email(SOME_TIMESTAMP, SOME_TIMESTAMP, SOME_STRING, SOME_MESSAGE, SOME_EMAIL_ENDPOINT,
			asArray(SOME_EMAIL_ENDPOINT), asArray(SOME_EMAIL_ENDPOINT, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME), asArray(SOME_EMAIL_ENDPOINT));

	static {
		SOME_DISPLAYED_NAME.uid = "00";
		SOME_LOCALNAME.uid = "01";
		SOME_TOP_LEVEL_DOMAIN.uid = "02";
		SOME_DOMAIN.uid = "03";
		SOME_EMAIL_ADDRESS.uid = "04";
		SOME_EMAIL_ENDPOINT.uid = "05";
		EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME.uid = "0500";
		EMAIL_ENDPOINT_MISSING_ADDRESS.uid = "0501";
		SOME_EMAIL.uid = "06";
	}

	protected static <T> T[] asArray(@SuppressWarnings("unchecked") T... endpoints) {
		return endpoints;
	}
}
