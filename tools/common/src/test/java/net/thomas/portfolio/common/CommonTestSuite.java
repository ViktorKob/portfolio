package net.thomas.portfolio.common;

import org.junit.runners.Suite;

import net.thomas.portfolio.common.services.parameters.CredentialsUnitTest;
import net.thomas.portfolio.common.services.parameters.ParameterGroupUnitTest;
import net.thomas.portfolio.common.services.parameters.ServiceDependencyUnitTest;
import net.thomas.portfolio.common.services.parameters.validation.ValidationTestSuite;
import net.thomas.portfolio.common.utils.ToStringUtilUnitTest;

@Suite.SuiteClasses({ ToStringUtilUnitTest.class, ValidationTestSuite.class, CredentialsUnitTest.class, ParameterGroupUnitTest.class,
		ServiceDependencyUnitTest.class })
public class CommonTestSuite {
}