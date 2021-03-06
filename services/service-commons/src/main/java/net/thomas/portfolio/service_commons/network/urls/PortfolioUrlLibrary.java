package net.thomas.portfolio.service_commons.network.urls;

import static net.thomas.portfolio.common.services.parameters.ParameterGroup.asGroup;
import static net.thomas.portfolio.enums.AnalyticsServiceEndpoint.ANALYTICS_BASE;
import static net.thomas.portfolio.enums.AnalyticsServiceEndpoint.LOOKUP_KNOWLEDGE;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.DOCUMENTS;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.ENTITIES;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.FROM_SIMPLE_REP;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.INVERTED_INDEX;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.REFERENCES;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SAMPLES;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SCHEMA;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SELECTORS;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.STATISTICS;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SUGGESTIONS;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.AUDIT_LOG;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.HISTORY;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.INVERTED_INDEX_QUERY;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.LEGAL_ROOT;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.LEGAL_RULES;
import static net.thomas.portfolio.enums.LegalServiceEndpoint.STATISTICS_LOOKUP;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.AS_HTML;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.AS_SIMPLE_REPRESENTATION;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.AS_TEXT;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_ENTITY_ROOT;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_SELECTOR_ROOT;
import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.USAGE_ACTIVITIES;
import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.USAGE_ACTIVITIES_ROOT;
import static net.thomas.portfolio.service_commons.network.ServiceEndpointBuilder.asEndpoint;
import static net.thomas.portfolio.services.Service.ANALYTICS_SERVICE;
import static net.thomas.portfolio.services.Service.HBASE_INDEXING_SERVICE;
import static net.thomas.portfolio.services.Service.LEGAL_SERVICE;
import static net.thomas.portfolio.services.Service.RENDER_SERVICE;
import static net.thomas.portfolio.services.Service.USAGE_DATA_SERVICE;

import net.thomas.portfolio.common.services.parameters.SingleParameter;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

/***
 * URL generation for the infrastructure. These should not be cached, since they may leverage
 * service discovery during URL creation.
 */
public class PortfolioUrlLibrary {
	private final UrlFactory urlFactory;

	public EntityUrls entityUrls;
	public DocumentUrls documentUrls;
	public SelectorUrls selectorUrls;

	public PortfolioUrlLibrary(UrlFactory urlFactory) {
		this.urlFactory = urlFactory;
		entityUrls = new EntityUrls();
		documentUrls = new DocumentUrls();
		selectorUrls = new SelectorUrls();
	}

	@Deprecated
	public UrlFactory getUrlFactory() {
		return urlFactory;
	}

	public EntityUrls entities() {
		return entityUrls;
	}

	public DocumentUrls documents() {
		return documentUrls;
	}

	public SelectorUrls selectors() {
		return selectorUrls;
	}

	public String schema() {
		return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, SCHEMA);
	}

	public class EntityUrls {
		public final RenderUrls renderUrls;

		public EntityUrls() {
			renderUrls = new RenderUrls();
		}

		public RenderUrls render() {
			return renderUrls;
		}

		public String samples(String dataType, Integer amount) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(ENTITIES, dataType, SAMPLES), asGroup(new SingleParameter("amount", amount)));
		}

		public String lookup(DataTypeId id) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(ENTITIES, id));
		}

		public class RenderUrls {
			public String text(DataTypeId id) {
				return urlFactory.buildUrl(RENDER_SERVICE, asEndpoint(RENDER_ENTITY_ROOT, id, AS_TEXT));
			}

			public String html(DataTypeId id) {
				return urlFactory.buildUrl(RENDER_SERVICE, asEndpoint(RENDER_ENTITY_ROOT, id, AS_HTML));
			}
		}
	}

	public class DocumentUrls {
		public final RenderUrls renderUrls;

		public DocumentUrls() {
			renderUrls = new RenderUrls();
		}

		public RenderUrls render() {
			return renderUrls;
		}

		public String samples(String dataType, int amount) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(DOCUMENTS, dataType, SAMPLES), asGroup(new SingleParameter("amount", amount)));
		}

		public String lookup(DataTypeId id) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(DOCUMENTS, id));
		}

		public String references(DataTypeId documentId) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(DOCUMENTS, documentId, REFERENCES));
		}

		public String usageActivities(DataTypeId documentId) {
			return urlFactory.buildUrl(USAGE_DATA_SERVICE, asEndpoint(USAGE_ACTIVITIES_ROOT, documentId, USAGE_ACTIVITIES));
		}

		public String usageActivities(DataTypeId documentId, UsageActivity activity) {
			return urlFactory.buildUrl(USAGE_DATA_SERVICE, asEndpoint(USAGE_ACTIVITIES_ROOT, documentId, USAGE_ACTIVITIES), activity);
		}

		public String usageActivities(DataTypeId documentId, Bounds bounds) {
			return urlFactory.buildUrl(USAGE_DATA_SERVICE, asEndpoint(USAGE_ACTIVITIES_ROOT, documentId, USAGE_ACTIVITIES), bounds);
		}

		public class RenderUrls {
			public String text(DataTypeId id) {
				return urlFactory.buildUrl(RENDER_SERVICE, asEndpoint(RENDER_ENTITY_ROOT, id, AS_TEXT));
			}

			public String html(DataTypeId id) {
				return urlFactory.buildUrl(RENDER_SERVICE, asEndpoint(RENDER_ENTITY_ROOT, id, AS_HTML));
			}
		}
	}

	public class SelectorUrls {
		public final RenderUrls renderUrls;
		public final HistoryUrls historyUrls;
		public final AuditUrls auditUrls;

		public SelectorUrls() {
			renderUrls = new RenderUrls();
			auditUrls = new AuditUrls();
			historyUrls = new HistoryUrls();
		}

		public RenderUrls render() {
			return renderUrls;
		}

		public HistoryUrls history() {
			return historyUrls;
		}

		public AuditUrls audit() {
			return auditUrls;
		}

		public String samples(String dataType, int amount) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, dataType, SAMPLES), asGroup(new SingleParameter("amount", amount)));
		}

		public String suggestions(String simpleRepresentation) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, SUGGESTIONS, simpleRepresentation));
		}

		public String lookup(DataTypeId id) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, id));
		}

		public String fromSimpleRepresentation(String dataType, String simpleRepresentation) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, dataType, FROM_SIMPLE_REP, simpleRepresentation));
		}

		public String invertedIndex(DataTypeId selectorId) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, selectorId, INVERTED_INDEX));
		}

		public String invertedIndex(InvertedIndexLookupRequest request) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, request.getSelectorId(), INVERTED_INDEX), request.getGroups());
		}

		public String knowledge(DataTypeId selectorId) {
			return urlFactory.buildUrl(ANALYTICS_SERVICE, asEndpoint(ANALYTICS_BASE, selectorId, LOOKUP_KNOWLEDGE));
		}

		public String statistics(DataTypeId selectorId) {
			return urlFactory.buildUrl(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, selectorId, STATISTICS));
		}

		public class RenderUrls {
			public String simpleRepresentation(DataTypeId selectorId) {
				return urlFactory.buildUrl(RENDER_SERVICE, asEndpoint(RENDER_SELECTOR_ROOT, selectorId, AS_SIMPLE_REPRESENTATION));
			}

			public String text(DataTypeId id) {
				return urlFactory.buildUrl(RENDER_SERVICE, asEndpoint(RENDER_ENTITY_ROOT, id, AS_TEXT));
			}

			public String html(DataTypeId id) {
				return urlFactory.buildUrl(RENDER_SERVICE, asEndpoint(RENDER_ENTITY_ROOT, id, AS_HTML));
			}
		}

		public class AuditUrls {
			public final CheckUrls checkUrls;
			public final LogUrls logUrls;

			public AuditUrls() {
				checkUrls = new CheckUrls();
				logUrls = new LogUrls();
			}

			public CheckUrls check() {
				return checkUrls;
			}

			public LogUrls log() {
				return logUrls;
			}

			public class CheckUrls {
				public String invertedIndex(DataTypeId selectorId, LegalInformation legalInfo) {
					return urlFactory.buildUrl(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, selectorId, INVERTED_INDEX_QUERY, LEGAL_RULES), legalInfo);
				}

				public String statistics(DataTypeId selectorId, LegalInformation legalInfo) {
					return urlFactory.buildUrl(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, selectorId, STATISTICS_LOOKUP, LEGAL_RULES), legalInfo);
				}
			}

			public class LogUrls {
				public String invertedIndex(DataTypeId selectorId, LegalInformation legalInfo) {
					return urlFactory.buildUrl(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, selectorId, INVERTED_INDEX_QUERY, AUDIT_LOG), legalInfo);
				}

				public String statistics(DataTypeId selectorId, LegalInformation legalInfo) {
					return urlFactory.buildUrl(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, selectorId, STATISTICS_LOOKUP, AUDIT_LOG), legalInfo);
				}
			}
		}

		public class HistoryUrls {
			public String all() {
				return urlFactory.buildUrl(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, HISTORY));
			}

			public String item(int itemId) {
				return urlFactory.buildUrl(LEGAL_SERVICE, asEndpoint(LEGAL_ROOT, HISTORY, "" + itemId));
			}
		}
	}
}