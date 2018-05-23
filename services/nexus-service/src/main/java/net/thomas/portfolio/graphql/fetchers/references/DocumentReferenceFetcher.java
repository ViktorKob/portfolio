package net.thomas.portfolio.graphql.fetchers.references;

import java.util.Collection;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class DocumentReferenceFetcher extends ModelDataFetcher<Collection<Reference>> {

	public DocumentReferenceFetcher(HbaseModelAdaptor adaptor) {
		super(adaptor, 50);
	}

	@Override
	public Collection<Reference> _get(DataFetchingEnvironment environment) {
		final Document document = (Document) environment.getSource();
		return adaptor.getReferences(document);
	}
}