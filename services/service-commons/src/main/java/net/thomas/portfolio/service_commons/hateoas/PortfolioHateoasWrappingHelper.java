package net.thomas.portfolio.service_commons.hateoas;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.Link.REL_FIRST;
import static org.springframework.hateoas.Link.REL_LAST;
import static org.springframework.hateoas.Link.REL_NEXT;
import static org.springframework.hateoas.Link.REL_PREVIOUS;
import static org.springframework.hateoas.Link.REL_SELF;

import java.util.LinkedList;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

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
import net.thomas.portfolio.shared_objects.legal.HistoryItem;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;

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

	public ResourceSupport wrap(DataType entity) {
		final Resource<DataType> container = new Resource<>(entity);
		if (entity instanceof Document) {
			container.add(asLink(REL_SELF, urlLibrary.hbase.documents.lookup(entity.getId())));
			container.add(asLink("references", urlLibrary.hbase.documents.references(entity.getId())));
			container.add(asLink("usageData", urlLibrary.usageData.usageActivities(entity.getId())));
		} else if (entity instanceof Selector) {
			container.add(asLink(REL_SELF, urlLibrary.hbase.selectors.lookup(entity.getId())));
			container.add(asLink("statistics", urlLibrary.hbase.selectors.statistics(entity.getId())));
			container.add(asLink("invertedIndex", urlLibrary.hbase.selectors.invertedIndex(entity.getId())));
			container.add(asLink("analytics", urlLibrary.analytics.knowledge(entity.getId())));
		} else {
			container.add(asLink(REL_SELF, urlLibrary.hbase.entities.lookup(entity.getId())));
		}
		return container;
	}

	public Resource<Statistics> wrap(final Statistics statistics, final DataTypeId id) {
		final Resource<Statistics> container = new Resource<>(statistics);
		container.add(asLink(REL_SELF, urlLibrary.hbase.selectors.statistics(id)));
		container.add(asLink("selector", urlLibrary.hbase.selectors.lookup(id)));
		return container;
	}

	public Resource<References> wrap(final References references, final DataTypeId documentId) {
		final Resource<References> container = new Resource<>(references);
		container.add(asLink(REL_SELF, urlLibrary.hbase.documents.references(documentId)));
		container.add(asLink("document", urlLibrary.hbase.documents.lookup(documentId)));
		return container;
	}

	public ResourceSupport wrap(DocumentInfos infos, DataTypeId id) {
		final Resources<ResourceSupport> container = new Resources<>(infos.getInfos().stream().map(this::wrap).collect(toList()));
		container.add(asLink(REL_SELF, urlLibrary.hbase.selectors.invertedIndex(id)));
		return container;
	}

	public ResourceSupport wrap(Entities samples, String type, int amount) {
		final Resources<ResourceSupport> container = new Resources<>(samples.getEntities().stream().map(this::wrap).collect(toList()));
		container.add(asLink("more", urlLibrary.hbase.entities.samples(type, amount)));
		return container;
	}

	public ResourceSupport wrap(DocumentInfo info) {
		final Resource<DocumentInfo> container = new Resource<>(info);
		container.add(asLink("document", urlLibrary.hbase.documents.lookup(info.getId())));
		return container;
	}

	public ResourceSupport wrap(List<Selector> suggestions, String simpleRepresentation) {
		final Resources<ResourceSupport> container = new Resources<>(suggestions.stream().map(this::wrap).collect(toList()));
		container.add(asLink(REL_SELF, urlLibrary.hbase.selectors.suggestions(simpleRepresentation)));
		return container;
	}

	public ResourceSupport wrap(List<HistoryItem> items) {
		final List<ResourceSupport> packedItems = items.stream().map(this::wrapInner).collect(toList());
		final PageMetadata metaData = new PageMetadata(10, 1, packedItems.size());
		final Resources<ResourceSupport> container = new PagedResources<>(packedItems, metaData);
		container.add(asLink(REL_SELF, urlLibrary.legal.history.all()));
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
		container.add(asLink(REL_SELF, urlLibrary.legal.history.item(item.getItemId())));
		container.add(asLink("all", urlLibrary.legal.history.all()));
		container.add(asLink("selector", urlLibrary.hbase.selectors.lookup(item.getSelectorId())));
		return container;
	}

	private void addNeighbourLinks(ResourceSupport container, final HistoryItem item, int highestId) {
		if (item.getItemId() > 0) {
			container.add(asLink(REL_PREVIOUS, urlLibrary.legal.history.item(item.getItemId() - 1)));
		}
		if (item.getItemId() < highestId) {
			container.add(asLink(REL_NEXT, urlLibrary.legal.history.item(item.getItemId() + 1)));
		}
	}

	private void addBorderLinks(ResourceSupport container, final HistoryItem item, int highestId) {
		if (highestId > -1) {
			container.add(asLink(REL_FIRST, urlLibrary.legal.history.item(0)));
			container.add(asLink(REL_LAST, urlLibrary.legal.history.item(highestId)));
		}
	}

	public ResourceSupport wrap(AnalyticalKnowledge priorKnowledge, DataTypeId selectorId) {
		final Resource<AnalyticalKnowledge> container = new Resource<>(priorKnowledge);
		container.add(asLink(REL_SELF, urlLibrary.analytics.knowledge(selectorId)));
		container.add(asLink("selector", urlLibrary.hbase.selectors.lookup(selectorId)));
		return container;
	}

	public ResourceSupport wrapSimpleRepresentation(String render, DataTypeId selectorId) {
		final Resource<String> container = new Resource<>(render);
		container.add(asLink(REL_SELF, urlLibrary.render.simpleRepresentation(selectorId)));
		container.add(asLink("selector", urlLibrary.hbase.selectors.lookup(selectorId)));
		return container;
	}

	public ResourceSupport wrapTextualRepresentation(String text, DataTypeId id) {
		final Resource<String> container = new Resource<>(text);
		container.add(asLink(REL_SELF, urlLibrary.render.text(id)));
		container.add(asLink("entity", urlLibrary.hbase.selectors.lookup(id)));
		return container;
	}

	public ResourceSupport wrapHtmlRepresentation(String html, DataTypeId id) {
		final Resource<String> container = new Resource<>(html);
		container.add(asLink(REL_SELF, urlLibrary.render.html(id)));
		container.add(asLink("entity", urlLibrary.hbase.selectors.lookup(id)));
		return container;
	}

	public ResourceSupport wrapInvertedIndexLegalStatus(Legality response, DataTypeId selectorId, LegalInformation legalInfo) {
		final Resource<Legality> container = new Resource<>(response);
		container.add(asLink(REL_SELF, urlLibrary.legal.audit.check.invertedIndex(selectorId, legalInfo)));
		container.add(asLink("selector", urlLibrary.hbase.selectors.lookup(selectorId)));
		return container;
	}

	public ResourceSupport wrapStatisticsLegalStatus(Legality response, DataTypeId selectorId, LegalInformation legalInfo) {
		final Resource<Legality> container = new Resource<>(response);
		container.add(asLink(REL_SELF, urlLibrary.legal.audit.check.statistics(selectorId, legalInfo)));
		container.add(asLink("selector", urlLibrary.hbase.selectors.lookup(selectorId)));
		return container;
	}

	public ResourceSupport wrap(UsageActivities activities, DataTypeId id, Bounds bounds) {
		final Resource<UsageActivities> container = new Resource<>(activities);
		container.add(asLink(REL_SELF, urlLibrary.usageData.usageActivities(id, bounds)));
		container.add(asLink("document", urlLibrary.hbase.documents.lookup(id)));
		return container;
	}

	private Link asLink(final String relation, final String url) {
		return new Link(url, relation);
	}
}