package net.thomas.portfolio.testing_tools;

import static net.thomas.portfolio.testing_tools.ReflectionUtil.buildAllPossibleInstancesWithOneFieldSetToNull;
import static net.thomas.portfolio.testing_tools.ReflectionUtil.copyInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

public class EqualsTestUtil {

	public static void assertBasicEqualsIsValid(Object object) {
		assertEquals(object, object);
		assertNotEquals(object, null);
		assertNotEquals(object, "");
		assertEquals(object, copyInstance(object));
	}

	public static void assertEqualsIsValidIncludingNullChecks(Object object) {
		assertBasicEqualsIsValid(object);
		final List<Object> firstInstances = buildAllPossibleInstancesWithOneFieldSetToNull(object);
		final List<Object> secondInstances = buildAllPossibleInstancesWithOneFieldSetToNull(object);
		for (int i = 0; i < firstInstances.size(); i++) {
			assertComparisonCombinationsWorkAsExpected(object, firstInstances.get(i), secondInstances.get(i));
		}
	}

	private static void assertComparisonCombinationsWorkAsExpected(Object object, final Object newInstance1, final Object newInstance2) {
		assertFalse("Comparisson of object to different object type had unexpected outcome for " + object + " against " + newInstance1,
				object.equals(newInstance1));
		assertFalse("Comparisson of object to different object type had unexpected outcome for " + newInstance1 + " against " + object,
				newInstance1.equals(object));
		assertTrue("Comparisson of object to different object type had unexpected outcome for " + object + " against " + newInstance2,
				newInstance1.equals(newInstance2));
	}

}
