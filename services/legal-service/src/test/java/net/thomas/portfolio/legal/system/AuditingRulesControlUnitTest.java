package net.thomas.portfolio.legal.system;

import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.POSSIBLY;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.UNLIKELY;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.service_commons.adaptors.impl.AnalyticsAdaptorImpl;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.Legality;

public class AuditingRulesControlUnitTest {
	private static final String SELECTOR_TYPE = "TYPE";
	private static final String UID = "FF";

	private AnalyticsAdaptorImpl analyticsAdaptor;
	private DataTypeId selectorId;
	private LegalInfoForTestBuilder legalInfoBuilder;
	private AuditingRulesControl auditingSystem;

	@Before
	public void setupForTests() {
		analyticsAdaptor = mock(AnalyticsAdaptorImpl.class);
		selectorId = new DataTypeId(SELECTOR_TYPE, UID);
		legalInfoBuilder = new LegalInfoForTestBuilder();
		auditingSystem = new AuditingRulesControl();
		auditingSystem.setAnalyticsAdaptor(analyticsAdaptor);
	}

	@Test
	public void searchingForUnrestrictedSelectorWithValidUserIsLegal() {
		legalInfoBuilder.setValidUser();
		setupAnalyticsServiceToRespondSelectorIsUnrestricted();
		final Legality legality = auditingSystem.checkLegalityOfSelectorQuery(selectorId, legalInfoBuilder.build());
		assertEquals(LEGAL, legality);
	}

	@Test
	public void searchingForSemiRestrictedSelectorWithValidUserIsLegal() {
		legalInfoBuilder.setValidUser();
		setupAnalyticsServiceToRespondSelectorIsPartiallyRestricted();
		final Legality legality = auditingSystem.checkLegalityOfSelectorQuery(selectorId, legalInfoBuilder.build());
		assertEquals(LEGAL, legality);
	}

	@Test
	public void searchingWithNullUserIsIllegal() {
		legalInfoBuilder.setNullUser();
		setupAnalyticsServiceToRespondSelectorIsUnrestricted();
		final Legality legality = auditingSystem.checkLegalityOfSelectorQuery(selectorId, legalInfoBuilder.build());
		assertEquals(ILLEGAL, legality);
	}

	@Test
	public void searchingWithEmptyUserIsIllegal() {
		legalInfoBuilder.setEmptyUser();
		setupAnalyticsServiceToRespondSelectorIsUnrestricted();
		final Legality legality = auditingSystem.checkLegalityOfSelectorQuery(selectorId, legalInfoBuilder.build());
		assertEquals(ILLEGAL, legality);
	}

	@Test
	public void searchingForRestrictedSelectorWithNullJustificationIsIllegal() {
		legalInfoBuilder.setNullJustification();
		setupAnalyticsServiceToRespondSelectorIsRestricted();
		final Legality legality = auditingSystem.checkLegalityOfSelectorQuery(selectorId, legalInfoBuilder.build());
		assertEquals(ILLEGAL, legality);
	}

	@Test
	public void searchingForRestrictedSelectorWithEmptyJustificationIsIllegal() {
		legalInfoBuilder.setEmptyJustification();
		setupAnalyticsServiceToRespondSelectorIsRestricted();
		final Legality legality = auditingSystem.checkLegalityOfSelectorQuery(selectorId, legalInfoBuilder.build());
		assertEquals(ILLEGAL, legality);
	}

	@Test
	public void searchingForRestrictedSelectorWithJustificationIsLegal() {
		legalInfoBuilder.setValidJustification();
		setupAnalyticsServiceToRespondSelectorIsRestricted();
		final Legality legality = auditingSystem.checkLegalityOfSelectorQuery(selectorId, legalInfoBuilder.build());
		assertEquals(LEGAL, legality);
	}

	private void setupAnalyticsServiceToRespondSelectorIsUnrestricted() {
		when(analyticsAdaptor.getKnowledge(eq(selectorId))).thenReturn(new AnalyticalKnowledge(null, UNLIKELY, UNLIKELY));
	}

	private void setupAnalyticsServiceToRespondSelectorIsPartiallyRestricted() {
		when(analyticsAdaptor.getKnowledge(eq(selectorId))).thenReturn(new AnalyticalKnowledge(null, UNLIKELY, POSSIBLY));
	}

	private void setupAnalyticsServiceToRespondSelectorIsRestricted() {
		when(analyticsAdaptor.getKnowledge(eq(selectorId))).thenReturn(new AnalyticalKnowledge(null, UNLIKELY, CERTAIN));
	}
}
