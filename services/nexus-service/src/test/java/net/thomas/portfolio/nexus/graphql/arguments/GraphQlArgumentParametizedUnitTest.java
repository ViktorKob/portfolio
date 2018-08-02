package net.thomas.portfolio.nexus.graphql.arguments;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.USAGE_ACTIVITY_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;

@RunWith(Parameterized.class)
public class GraphQlArgumentParametizedUnitTest {
	private static final Object SOME_OBJECT = "some object";

	@Parameters
	public static Collection<GraphQlArgument> arguments() {
		return stream(GraphQlArgument.values()).filter(argument -> argument != AFTER_DATE && argument != BEFORE_DATE && argument != USAGE_ACTIVITY_TYPE)
			.collect(toList());
	}

	private final GraphQlArgument argumentUnderTest;
	private DataFetchingEnvironment environment;

	@BeforeClass
	public static void setUpGraphQlArguments() {
		GraphQlArgument.initialize(new Adaptors.Builder().build());
	}

	public GraphQlArgumentParametizedUnitTest(GraphQlArgument argumentUnderTest) {
		this.argumentUnderTest = argumentUnderTest;
	}

	@Before
	public void setUpForTest() {
		environment = mock(DataFetchingEnvironment.class);
	}

	@Test
	public void shouldCheckIfValueForArgumentIsAvailable() {
		when(environment.containsArgument(eq(argumentUnderTest.getName()))).thenReturn(true);
		assertTrue(argumentUnderTest.canBeExtractedFrom(environment));
	}

	@Test
	public void shouldFetchValueForArgument() {
		when(environment.getArgument(eq(argumentUnderTest.getName()))).thenReturn(SOME_OBJECT);
		final Object actualObject = argumentUnderTest.extractFrom(environment);
		assertEquals(SOME_OBJECT, actualObject);
	}
}
