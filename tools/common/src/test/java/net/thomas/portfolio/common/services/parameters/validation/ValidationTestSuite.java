package net.thomas.portfolio.common.services.parameters.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ EnumStringValueValidatorUnitTest.class, EnumValueValidatorUnitTest.class, IntegerRangeValidatorUnitTest.class, LongRangeValidatorUnitTest.class, StringPresenceValidatorUnitTest.class, SpecificStringPresenceValidatorUnitTest.class })
public class ValidationTestSuite {
}