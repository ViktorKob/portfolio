package net.thomas.portfolio.service_commons;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.service_commons.network.NetworkTestSuite;
import net.thomas.portfolio.service_commons.validation.UidValidatorUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ /* AdaptorsUnitTest.class, */ NetworkTestSuite.class, UidValidatorUnitTest.class })
public class ServiceCommonsTestSuite {
}