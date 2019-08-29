package net.thomas.portfolio.legal.system;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class AuditLoggingControlUnitTest {
	private static final String SELECTOR_TYPE = "TYPE";
	private static final String UID = "FF";

	private DataTypeId selectorId;
	private LegalInfoForTestBuilder legalInfoBuilder;
	private AuditLoggingControl auditLoggingSystem;

	@Before
	public void setupForTests() {
		selectorId = new DataTypeId(SELECTOR_TYPE, UID);
		legalInfoBuilder = new LegalInfoForTestBuilder();
		auditLoggingSystem = new AuditLoggingControl();
	}

	@Test
	public void shouldReturnIdAfterAuditLoggingInvertedIndexLookup() {
		final int itemId = auditLoggingSystem.logInvertedIndexLookup(selectorId, legalInfoBuilder.build());
		assertEquals(auditLoggingSystem.getLastId(), itemId);
	}

	@Test
	public void shouldReturnIdAfterAuditLoggingStatisticsLookup() {
		final int itemId = auditLoggingSystem.logStatisticsLookup(selectorId, legalInfoBuilder.build());
		assertEquals(auditLoggingSystem.getLastId(), itemId);
	}
}