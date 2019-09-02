package net.thomas.portfolio.hateoas;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.DOCUMENTS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.ENTITIES_PATH;
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

	public String getScemaRootUrl() {
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

	public String getEntityUrl(String entityType, String entityId) {
		return buildEntityUrl(getEntityRootUrl(), entityType, entityId);
	}

	public String getDocumentUrl(String entityType, String entityId) {
		return buildEntityUrl(getDocumentRootUrl(), entityType, entityId);
	}

	public String getSelectorUrl(String entityType, String entityId) {
		return buildEntityUrl(getSelectorRootUrl(), entityType, entityId);
	}

	public String getStatisticsUrl(String entityType, String entityId) {
		return buildEntityUrl(getSelectorRootUrl(), entityType, entityId) + STATISTICS_PATH;
	}

	private String buildEntityUrl(String prefix, String entityType, String entityId) {
		return prefix + slash(entityType) + slash(entityId);
	}
}