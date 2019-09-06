package net.thomas.portfolio.service_commons.hateoas;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.Link.REL_FIRST;
import static org.springframework.hateoas.Link.REL_LAST;
import static org.springframework.hateoas.Link.REL_NEXT;
import static org.springframework.hateoas.Link.REL_PREVIOUS;
import static org.springframework.hateoas.Link.REL_SELF;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.legal.HistoryItem;

public class PortfolioHateoasWrappingHelper {
	private final PortfolioUrlLibrary urlLibrary;

	public PortfolioHateoasWrappingHelper(UrlFactory urlFactory) {
		urlLibrary = new PortfolioUrlLibrary(urlFactory);
	}

	public ResourceSupport wrap(DataType entity) {
		final Resource<DataType> packed = new Resource<>(entity);
		if (entity instanceof Document) {
			packed.add(buildDocumentLink(REL_SELF, entity.getId()));
		} else if (entity instanceof Selector) {
			packed.add(buildSelectorLink(REL_SELF, entity.getId()));
			packed.add(buildStatisticsLink("statistics", entity.getId()));
			packed.add(buildInvertedIndexLink("invertedIndex", entity.getId()));
		} else {
			packed.add(buildEntityLink(REL_SELF, entity.getId()));
		}
		return packed;
	}

	public Resource<Statistics> wrap(final DataTypeId id, final Statistics statistics) {
		final Resource<Statistics> packed = new Resource<>(statistics);
		packed.add(buildStatisticsLink(REL_SELF, id));
		packed.add(buildSelectorLink("selector", id));
		return packed;
	}

	public ResourceSupport wrap(DataTypeId id, DocumentInfos infos) {
		final Resources<ResourceSupport> packed = new Resources<>(infos.getInfos().stream().map(this::wrap).collect(toList()));
		packed.add(buildInvertedIndexLink(REL_SELF, id));
		return packed;
	}

	public ResourceSupport wrap(String type, int amount, Entities samples) {
		final Resources<ResourceSupport> packed = new Resources<>(samples.getEntities().stream().map(this::wrap).collect(toList()));
		packed.add(buildSampleLink("more", type, amount));
		return packed;
	}

	public ResourceSupport wrap(DocumentInfo info) {
		final Resource<DocumentInfo> packed = new Resource<>(info);
		packed.add(buildDocumentLink(REL_SELF, info.getId()));
		return packed;
	}

	public ResourceSupport wrap(String simpleRepresentation, List<Selector> suggestions) {
		final Resources<ResourceSupport> packed = new Resources<>(suggestions.stream().map(this::wrap).collect(toList()));
		packed.add(asLink(REL_SELF, urlLibrary.hbase.selectors.suggestions(simpleRepresentation)));
		return packed;
	}

	public ResourceSupport wrap(List<HistoryItem> items) {
		final List<ResourceSupport> packedItems = items.stream().map(this::wrapInner).collect(toList());
		final PageMetadata metaData = new PageMetadata(10, 1, packedItems.size());
		final Resources<ResourceSupport> packed = new PagedResources<>(packedItems, metaData);
		packed.add(buildHistoryLink(REL_SELF));
		return packed;
	}

	public ResourceSupport wrap(HistoryItem item, int highestId) {
		final ResourceSupport packed = wrapInner(item);
		addNeighbourLinks(packed, item, highestId);
		addBorderLinks(packed, item, highestId);
		return packed;
	}

	private ResourceSupport wrapInner(HistoryItem item) {
		final Resource<HistoryItem> packed = new Resource<>(item);
		packed.add(buildHistoryItemLink(REL_SELF, item.getItemId()));
		packed.add(buildHistoryLink("all"));
		return packed;
	}

	private void addNeighbourLinks(ResourceSupport packed, final HistoryItem item, int highestId) {
		if (item.getItemId() > 0) {
			packed.add(buildHistoryItemLink(REL_PREVIOUS, item.getItemId() - 1));
		}
		if (item.getItemId() < highestId) {
			packed.add(buildHistoryItemLink(REL_NEXT, item.getItemId() + 1));
		}
	}

	private void addBorderLinks(ResourceSupport packed, final HistoryItem item, int highestId) {
		if (highestId > -1) {
			packed.add(buildHistoryItemLink(REL_FIRST, 0));
			packed.add(buildHistoryItemLink(REL_LAST, highestId));
		}
	}

	private Link buildDocumentLink(String relation, DataTypeId id) {
		return asLink(relation, urlLibrary.hbase.documents.lookup(id));
	}

	private Link buildSelectorLink(String relation, DataTypeId id) {
		return asLink(relation, urlLibrary.hbase.selectors.lookup(id));
	}

	private Link buildStatisticsLink(String relation, DataTypeId id) {
		return asLink(relation, urlLibrary.hbase.selectors.statistics(id));
	}

	private Link buildInvertedIndexLink(String relation, DataTypeId id) {
		return asLink(relation, urlLibrary.hbase.selectors.invertedIndex(id));
	}

	private Link buildEntityLink(String relation, DataTypeId id) {
		return asLink(relation, urlLibrary.hbase.entities.lookup(id));
	}

	private Link buildSampleLink(String relation, String type, int amount) {
		return asLink(relation, urlLibrary.hbase.entities.samples(type, amount));
	}

	private Link buildHistoryLink(String relation) {
		return asLink(relation, urlLibrary.legal.history.all());
	}

	private Link buildHistoryItemLink(String relation, int itemId) {
		return asLink(relation, urlLibrary.legal.history.item(itemId));
	}

	private Link asLink(final String relation, final String url) {
		return new Link(url, relation);
	}
}