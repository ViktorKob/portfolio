package net.thomas.portfolio.nexus.service.test_utils;

import static java.util.Collections.singletonList;
import static net.thomas.portfolio.nexus.service.test_utils.BoundMatcher.matches;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestUtil.DOCUMENT_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestUtil.EXAMPLE_IDS;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestUtil.SOME_USAGE_ACTIVITIES;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestUtil.SOME_USAGE_ACTIVITY;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestUtil.SOME_USAGE_ACTIVITY_TYPE;
import static net.thomas.portfolio.nexus.service.test_utils.GraphQlTestUtil.SOME_USER;
import static net.thomas.portfolio.nexus.service.test_utils.UsageActivityMatcher.matches;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument;
import net.thomas.portfolio.service_commons.adaptors.specific.UsageAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;

public class UsageActivityTestUtil {
	private final GraphQlQueryBuilder queryBuilder;
	private final UsageAdaptor adaptor;
	private final GraphQlQueryTestExecutionUtil executionUtil;

	public UsageActivityTestUtil(final GraphQlQueryBuilder queryBuilder, final UsageAdaptor adaptor, final GraphQlQueryTestExecutionUtil executionUtil) {
		this.queryBuilder = queryBuilder;
		this.adaptor = adaptor;
		this.executionUtil = executionUtil;
	}

	public void assertThatFetchUsageActivityWithArgumentFunctionsCorrectly(final GraphQlArgument argument, final Object value) {
		assertThatFetchUsageActivityWithArgumentFunctionsCorrectly(argument, value, argument, value);
	}

	public void assertThatFetchUsageActivityWithArgumentFunctionsCorrectly(final GraphQlArgument argumentToLookUp, final Object valueToLookUp,
			final GraphQlArgument argumentToLookFor, final Object valueToLookFor) {
		final DataTypeId someId = EXAMPLE_IDS.get(DOCUMENT_TYPE);
		when(adaptor.fetchUsageActivities(eq(someId), argThat(matches(bound(argumentToLookFor), valueToLookFor)))).thenReturn(SOME_USAGE_ACTIVITIES);
		SOME_USAGE_ACTIVITIES.setActivities(singletonList(SOME_USAGE_ACTIVITY));
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidAndUsageActivityArgumentToFieldValueQuery(DOCUMENT_TYPE, argumentToLookUp, valueToLookUp, "activityType");
		assertEquals(SOME_USAGE_ACTIVITY_TYPE.name(),
				executionUtil.executeQueryAndLookupResponseAtPath(queryBuilder.build(), "data", DOCUMENT_TYPE, "usageActivities", "activityType"));
	}

	public void assertThatFetchUsageActivityWithInvalidArgumentReturnErrorMessageFragments(final GraphQlArgument argument, final Object value,
			final String... fragments) {
		final DataTypeId someId = EXAMPLE_IDS.get(DOCUMENT_TYPE);
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.setUidAndUsageActivityArgumentToFieldValueQuery(DOCUMENT_TYPE, argument, value, "activityType");
		final String message = executionUtil.executeQueryAndLookupResponseAtPath(queryBuilder.build(), "errors", "message").toString();
		for (final String fragment : fragments) {
			assertTrue(message.contains(fragment));
		}
	}

	private Field bound(final GraphQlArgument argument) {
		try {
			return Bounds.class.getField(argument.getName());
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Unable to locate field " + argument.getName() + " in Bounds");
		}
	}

	public void assertDefaultMutationReturnsCorrectValueForField(final String field, final Object value) {
		final DataTypeId someId = EXAMPLE_IDS.get(DOCUMENT_TYPE);
		when(adaptor.storeUsageActivity(eq(someId), argThat(matches(SOME_USER, SOME_USAGE_ACTIVITY_TYPE)))).thenReturn(SOME_USAGE_ACTIVITY);
		setupDefaultStoreUsageActivityCall(someId);
		queryBuilder.setUidActivityAndDocumentTypeToUsageActivityMutation(DOCUMENT_TYPE, field);
		assertEquals(value, executionUtil.executeMutationAndLookupResponseAtPath(queryBuilder.build(), "data", "usageActivity", DOCUMENT_TYPE, "add", field));
	}

	public void setupDefaultStoreUsageActivityCall(final DataTypeId someId) {
		queryBuilder.markAsMutation();
		queryBuilder.addVariable("uid", someId.uid);
		queryBuilder.addVariable("activityType", SOME_USAGE_ACTIVITY_TYPE.name());
		queryBuilder.addVariable("user", SOME_USER);
	}
}