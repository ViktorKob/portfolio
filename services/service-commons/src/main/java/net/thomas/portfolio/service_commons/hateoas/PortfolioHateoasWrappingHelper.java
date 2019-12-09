package net.thomas.portfolio.service_commons.hateoas;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.Link.REL_FIRST;
import static org.springframework.hateoas.Link.REL_LAST;
import static org.springframework.hateoas.Link.REL_NEXT;
import static org.springframework.hateoas.Link.REL_PREVIOUS;
import static org.springframework.hateoas.Link.REL_SELF;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.web.util.UriComponentsBuilder;

import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.legal.HistoryItem;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class PortfolioHateoasWrappingHelper {
	private final PortfolioUrlLibrary urlLibrary;

	public static <T> T unwrap(Resource<T> wrapped) {
		if (wrapped != null) {
			return wrapped.getContent();
		} else {
			return null;
		}
	}

	public static <T> List<T> unwrap(Resources<T> wrapped) {
		if (wrapped != null) {
			return new LinkedList<>(wrapped.getContent());
		} else {
			return null;
		}
	}

	public PortfolioHateoasWrappingHelper(UrlFactory urlFactory) {
		urlLibrary = new PortfolioUrlLibrary(urlFactory);
	}

	public ResourceSupport wrapWithRootLinks(String message, List<String> documentTypes, List<String> selectorTypes) {
		final Resource<String> index = new Resource<>(message);
		index.add(new Link(urlLibrary.selectors().history().all(), "Search history"));
		index.add(documentTypes.stream().map(type -> {
			return new Link(urlLibrary.entities().samples(type, 10), "Document samples: " + type);
		}).toArray(Link[]::new));
		index.add(selectorTypes.stream().map(type -> {
			return new Link(urlLibrary.selectors().samples(type, 10), "Selector samples: " + type);
		}).toArray(Link[]::new));
		return index;
	}

	public ResourceSupport wrap(HbaseIndexSchema schema) {
		final Resource<HbaseIndexSchema> container = new Resource<>(schema);
		container.add(asLink(REL_SELF, urlLibrary.schema()));
		return container;
	}

	public ResourceSupport wrap(DataType entity) {
		final Resource<DataType> container = new Resource<>(entity);
		if (entity instanceof Document) {
			container.add(asLink(REL_SELF, urlLibrary.documents().lookup(entity.getId())));
			container.add(asLink("references", urlLibrary.documents().references(entity.getId())));
			container.add(asLink("usageData", urlLibrary.documents().usageActivities(entity.getId())));
		} else if (entity instanceof Selector) {
			container.add(asLink(REL_SELF, urlLibrary.selectors().lookup(entity.getId())));
			container.add(asLink("statistics", urlLibrary.selectors().statistics(entity.getId())));
			container.add(asLink("invertedIndex", urlLibrary.selectors().invertedIndex(entity.getId())));
			container.add(asLink("analytics", urlLibrary.selectors().knowledge(entity.getId())));
		} else {
			container.add(asLink(REL_SELF, urlLibrary.entities().lookup(entity.getId())));
		}
		return container;
	}

	public Resource<Statistics> wrap(final Statistics statistics, final DataTypeId id) {
		final Resource<Statistics> container = new Resource<>(statistics);
		container.add(asLink(REL_SELF, urlLibrary.selectors().statistics(id)));
		container.add(asLink("selector", urlLibrary.selectors().lookup(id)));
		return container;
	}

	public Resource<References> wrap(final References references, final DataTypeId documentId) {
		final Resource<References> container = new Resource<>(references);
		container.add(asLink(REL_SELF, urlLibrary.documents().references(documentId)));
		container.add(asLink("document", urlLibrary.documents().lookup(documentId)));
		return container;
	}

	public ResourceSupport wrap(DocumentInfos infos, DataTypeId id) {
		final Resources<ResourceSupport> container = new Resources<>(infos.getInfos().stream().map(this::wrap).collect(toList()));
		container.add(asLink(REL_SELF, urlLibrary.selectors().invertedIndex(id)));
		return container;
	}

	public ResourceSupport wrap(DocumentInfos infos, DataTypeId id, InvertedIndexLookupRequest request, Pageable pageable) {
		final Resources<ResourceSupport> container = new Resources<>(infos.getInfos().stream().map(this::wrap).collect(toList()));
		container.add(asPagedLink(REL_SELF, urlLibrary.selectors().invertedIndex(id, request), pageable));
		return container;
	}

	public ResourceSupport wrap(DocumentInfo info) {
		final Resource<DocumentInfo> container = new Resource<>(info);
		container.add(asLink("document", urlLibrary.documents().lookup(info.getId())));
		return container;
	}

	public ResourceSupport wrap(Entities samples, String type, int amount) {
		final Resources<ResourceSupport> container = new Resources<>(samples.getEntities().stream().map(this::wrap).collect(toList()));
		container.add(asLink(REL_NEXT, urlLibrary.entities().samples(type, amount)));
		return container;
	}

	public ResourceSupport wrap(List<Selector> suggestions, String simpleRepresentation) {
		final Resources<ResourceSupport> container = new Resources<>(suggestions.stream().map(this::wrap).collect(toList()));
		container.add(asLink(REL_NEXT, urlLibrary.selectors().suggestions(simpleRepresentation)));
		return container;
	}

	public ResourceSupport wrap(HistoryItem item, int highestId) {
		final ResourceSupport container = wrapInner(item);
		addNeighbourLinks(container, item, highestId);
		addBorderLinks(container, item, highestId);
		return container;
	}

	private ResourceSupport wrapInner(HistoryItem item) {
		final Resource<HistoryItem> container = new Resource<>(item);
		container.add(asLink(REL_SELF, urlLibrary.selectors().history().item(item.getItemId())));
		container.add(asLink("all", urlLibrary.selectors().history().all()));
		container.add(asLink("selector", urlLibrary.selectors().lookup(item.getSelectorId())));
		return container;
	}

	private void addNeighbourLinks(ResourceSupport container, final HistoryItem item, int highestId) {
		if (item.getItemId() > 0) {
			container.add(asLink(REL_PREVIOUS, urlLibrary.selectors().history().item(item.getItemId() - 1)));
		}
		if (item.getItemId() < highestId) {
			container.add(asLink(REL_NEXT, urlLibrary.selectors().history().item(item.getItemId() + 1)));
		}
	}

	private void addBorderLinks(ResourceSupport container, final HistoryItem item, int highestId) {
		if (highestId > -1) {
			container.add(asLink(REL_FIRST, urlLibrary.selectors().history().item(0)));
			container.add(asLink(REL_LAST, urlLibrary.selectors().history().item(highestId)));
		}
	}

	public ResourceSupport wrap(AnalyticalKnowledge priorKnowledge, DataTypeId selectorId) {
		final Resource<AnalyticalKnowledge> container = new Resource<>(priorKnowledge);
		container.add(asLink(REL_SELF, urlLibrary.selectors().knowledge(selectorId)));
		container.add(asLink("selector", urlLibrary.selectors().lookup(selectorId)));
		return container;
	}

	public ResourceSupport wrapSimpleRepresentation(String render, DataTypeId selectorId) {
		final Resource<String> container = new Resource<>(render);
		container.add(asLink(REL_SELF, urlLibrary.selectors().render().simpleRepresentation(selectorId)));
		container.add(asLink("selector", urlLibrary.selectors().lookup(selectorId)));
		return container;
	}

	public ResourceSupport wrapTextualRepresentation(String text, DataTypeId id) {
		final Resource<String> container = new Resource<>(text);
		container.add(asLink(REL_SELF, urlLibrary.entities().render().text(id)));
		container.add(asLink("entity", urlLibrary.entities().lookup(id)));
		return container;
	}

	public ResourceSupport wrapHtmlRepresentation(String html, DataTypeId id) {
		final Resource<String> container = new Resource<>(html);
		container.add(asLink(REL_SELF, urlLibrary.entities().render().html(id)));
		container.add(asLink("entity", urlLibrary.entities().lookup(id)));
		return container;
	}

	public ResourceSupport wrapInvertedIndexLegalStatus(Legality response, DataTypeId selectorId, LegalInformation legalInfo) {
		final Resource<Legality> container = new Resource<>(response);
		container.add(asLink(REL_SELF, urlLibrary.selectors().audit().check().invertedIndex(selectorId, legalInfo)));
		container.add(asLink("selector", urlLibrary.selectors().lookup(selectorId)));
		return container;
	}

	public ResourceSupport wrapStatisticsLegalStatus(Legality response, DataTypeId selectorId, LegalInformation legalInfo) {
		final Resource<Legality> container = new Resource<>(response);
		container.add(asLink(REL_SELF, urlLibrary.selectors().audit().check().statistics(selectorId, legalInfo)));
		container.add(asLink("selector", urlLibrary.selectors().lookup(selectorId)));
		return container;
	}

	public ResourceSupport wrap(UsageActivities activities, DataTypeId documentId, Bounds bounds) {
		final Resource<UsageActivities> container = new Resource<>(activities);
		container.add(asLink(REL_SELF, urlLibrary.documents().usageActivities(documentId, bounds)));
		container.add(asLink("document", urlLibrary.documents().lookup(documentId)));
		return container;
	}

	public ResourceSupport wrap(UsageActivity activity, DataTypeId documentId) {
		final Resource<UsageActivity> container = new Resource<>(activity);
		container.add(asLink("document", urlLibrary.documents().lookup(documentId)));
		return container;
	}

	private Link asLink(final String relation, final String url) {
		return new Link(url, relation);
	}

	public Link asPagedLink(final String relation, final String url, Pageable page) {
		final UriComponentsBuilder builder = fromUriString(url).replaceQueryParam("page", page.getPageNumber()).replaceQueryParam("size", page.getPageSize());
		for (final Order sortingElement : page.getSort()) {
			builder.queryParam("sort", sortingElement.getProperty() + "-" + sortingElement.getDirection());
		}
		return new Link(builder.build().toString(), relation);
	}
}