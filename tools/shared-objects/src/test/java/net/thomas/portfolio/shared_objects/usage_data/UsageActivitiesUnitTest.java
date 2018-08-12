package net.thomas.portfolio.shared_objects.usage_data;

import static net.thomas.portfolio.shared_objects.usage_data.UsageActivityType.ANALYSED_DOCUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class UsageActivitiesUnitTest {
	private UsageActivities activities;
	private List<UsageActivity> actualContainer;

	@Before
	public void setUpForTest() {
		actualContainer = new LinkedList<>();
		activities = new UsageActivities(actualContainer);
		actualContainer.add(SOME_ACTIVITY);
	}

	@Test
	public void shouldBeEmptyInitially() {
		assertFalse(new UsageActivities().hasData());
	}

	@Test
	public void shouldOverrideOwnContainerOnDemand() {
		final UsageActivities activities = new UsageActivities();
		activities.setActivities(actualContainer);
		assertSame(actualContainer, activities.getActivities());
	}

	@Test
	public void shouldContainElementsWhenContainerDoes() {
		activities.setActivities(actualContainer);
		assertTrue(activities.hasData());
	}

	@Test
	public void shouldReturnElementByIndex() {
		activities.setActivities(actualContainer);
		assertEquals(SOME_ACTIVITY, activities.get(0));
	}

	@Test
	public void shouldForwardHashCodeCallToInnerContainer() {
		assertEquals(actualContainer.hashCode(), activities.hashCode());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void shouldForwardEqualsCallToInnerContainer() {
		assertTrue(activities.equals(actualContainer));
	}

	@Test
	public void shouldForwardToStringCallToInnerContainer() {
		assertEquals(actualContainer.toString(), activities.toString());
	}

	private static final UsageActivity SOME_ACTIVITY = new UsageActivity("User", ANALYSED_DOCUMENT, 1000L);
}
