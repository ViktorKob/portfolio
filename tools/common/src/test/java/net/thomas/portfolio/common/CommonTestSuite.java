package net.thomas.portfolio.common;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;

import net.thomas.portfolio.common.services.parameters.validation.ValidationTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ValidationTestSuite.class })
public class CommonTestSuite {
}