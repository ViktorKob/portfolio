package net.thomas.portfolio.hateoas;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.DOCUMENTS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.ENTITIES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.INVERTED_INDEX_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SCHEMA_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SELECTORS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.STATISTICS_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;

public class HbaseIndexUrlFactory extends PortfolioUrlFactory {
	private final String serviceRoot;

	public HbaseIndexUrlFactory(String globalUrlPrefix) {
		super(globalUrlPrefix);
		serviceRoot = globalUrlPrefix + HBASE_INDEXING_SERVICE_PATH;
	}

	public String getSchemaRootUrl() {
		return serviceRoot + SCHEMA_PATH;
	}

	public String getEntityRootUrl() {
		return serviceRoot + ENTITIES_PATH;
	}

	public String getDocumentRootUrl() {
		return serviceRoot + DOCUMENTS_PATH;
	}

	public String getSelectorRootUrl() {
		return serviceRoot + SELECTORS_PATH;
	}

	public String getSchemaUrl() {
		return getSchemaRootUrl();
	}

	public String getEntityUrl(String entityType, String entityId) {
		return buildEntityUrl(getEntityRootUrl(), entityType, entityId);
	}

	public String getDocumentUrl(String entityType, String entityId) {
		return buildEntityUrl(getDocumentRootUrl(), entityType, entityId);
	}

	public String getSelectorUrl(String entityType, String entityId) {
		return buildEntityUrl(getSelectorRootUrl(), entityType, entityId);
	}

	public String getEntitySampleUrl(String type, int amount) {
		return buildEntityTypeUrl(getEntityRootUrl(), type) + "?amount=" + amount;
	}

	public String getDocumentSampleUrl(String type, int amount) {
		return buildEntityTypeUrl(getDocumentRootUrl(), type) + "?amount=" + amount;
	}

	public String getSelectorSampleUrl(String type, int amount) {
		return buildEntityTypeUrl(getSelectorRootUrl(), type) + "?amount=" + amount;
	}

	public String getStatisticsUrl(String entityType, String entityId) {
		return buildEntityUrl(getSelectorRootUrl(), entityType, entityId) + STATISTICS_PATH;
	}

	public String getInvertedIndexUrl(String entityType, String entityId) {
		return buildEntityUrl(getSelectorRootUrl(), entityType, entityId) + INVERTED_INDEX_PATH;
	}

	public String getSelectorSuggestionsUrl(String simpleRepresentation) {
		return buildSelectorSuggestionUrl(getSelectorRootUrl(), simpleRepresentation);
	}

	private String buildSelectorSuggestionUrl(String prefix, String simpleRepresentation) {
		return prefix + slash(simpleRepresentation) + "/";
	}

	private String buildEntityTypeUrl(String prefix, String entityType) {
		return prefix + slash(entityType);
	}

	private String buildEntityUrl(String prefix, String entityType, String entityId) {
		return buildEntityTypeUrl(prefix, entityType) + slash(entityId);
	}
}