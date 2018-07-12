package net.thomas.portfolio.legal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.legal.service.LegalServiceControllerServiceAdaptorTest;
import net.thomas.portfolio.legal.system.AuditLoggingControlUnitTest;
import net.thomas.portfolio.legal.system.AuditingRulesControlUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AuditingRulesControlUnitTest.class, AuditLoggingControlUnitTest.class, LegalServiceControllerServiceAdaptorTest.class })
public class LegalServiceTestSuite {
}