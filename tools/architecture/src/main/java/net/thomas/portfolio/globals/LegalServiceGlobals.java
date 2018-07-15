package net.thomas.portfolio.globals;

public class LegalServiceGlobals {
	private static final String LEGAL_RULES = "/v1/legalRules";
	private static final String AUDIT_LOG = "/v1/auditLog";
	private static final String INVERTED_INDEX_LOOKUP = "/invertedIndexLookup";
	private static final String STATISTICS_LOOKUP = "/statisticsLookup";
	public static final String LEGALITY_OF_INVERTED_INDEX_QUERY_PATH = LEGAL_RULES + INVERTED_INDEX_LOOKUP;
	public static final String LEGALITY_OF_STATISTICS_LOOKUP_PATH = LEGAL_RULES + STATISTICS_LOOKUP;
	public static final String AUDIT_LOG_INVERTED_INDEX_LOOKUP_PATH = AUDIT_LOG + INVERTED_INDEX_LOOKUP;
	public static final String AUDIT_LOG_STATISTICS_LOOKUP_PATH = AUDIT_LOG + STATISTICS_LOOKUP;
}