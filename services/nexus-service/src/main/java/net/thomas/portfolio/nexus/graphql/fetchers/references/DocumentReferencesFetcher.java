package net.thomas.portfolio.nexus.graphql.fetchers.references;

import java.util.Collection;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;

public class DocumentReferencesFetcher extends ModelDataFetcher<Collection<Reference>> {

	public DocumentReferencesFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Collection<Reference> get(DataFetchingEnvironment environment) {
		return adaptors.getReferences(getId(environment));
	}
}