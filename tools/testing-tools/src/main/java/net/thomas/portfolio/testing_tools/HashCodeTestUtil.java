package net.thomas.portfolio.testing_tools;

import static net.thomas.portfolio.testing_tools.ReflectionUtil.buildAllPossibleInstancesWithOneFieldSetToNull;
import static net.thomas.portfolio.testing_tools.ReflectionUtil.copyInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

public class HashCodeTestUtil {
	public static void assertHashCodeIsValid(Object object) {
		assertEquals(object.hashCode(), copyInstance(object).hashCode());
	}

	public static void assertHashCodeIsValidIncludingNullChecks(Object object) {
		assertHashCodeIsValid(object);
		final List<Object> firstInstances = buildAllPossibleInstancesWithOneFieldSetToNull(object);
		final List<Object> secondInstances = buildAllPossibleInstancesWithOneFieldSetToNull(object);
		for (int i = 0; i < firstInstances.size(); i++) {
			assertHashCodeCombinationsWorkAsExpected(object, firstInstances.get(i), secondInstances.get(i));
		}
	}

	private static void assertHashCodeCombinationsWorkAsExpected(Object object, final Object newInstance1, final Object newInstance2) {
		assertNotEquals("Comparisson of hash codes for objects had unexpected outcome for " + object + " against " + newInstance1,
				object.hashCode() == newInstance1.hashCode());
		assertEquals("Comparisson of hash codes for objects had unexpected outcome for " + object + " against " + newInstance2, newInstance1.hashCode(),
				newInstance2.hashCode());
	}
}
