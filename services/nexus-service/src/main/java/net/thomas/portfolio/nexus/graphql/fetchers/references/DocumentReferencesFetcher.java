package net.thomas.portfolio.nexus.graphql.fetchers.references;

import java.util.Collection;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public class DocumentReferencesFetcher extends ModelDataFetcher<Collection<Reference>> {

	public DocumentReferencesFetcher(Adaptors adaptors) {
		super(adaptors/* , 50 */);
	}

	@Override
	public Collection<Reference> _get(DataFetchingEnvironment environment) {
		final Document document = (Document) extractOrFetchDataType(environment);
		return adaptors.getReferences(document.getId());
	}
}