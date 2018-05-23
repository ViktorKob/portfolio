package net.thomas.portfolio.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class SubTypeFetcher extends ModelDataFetcher<Datatype> {

	private final String fieldName;

	public SubTypeFetcher(String fieldName, HbaseModelAdaptor adaptor) {
		super(adaptor, 10);
		this.fieldName = fieldName;
	}

	@Override
	public Datatype _get(DataFetchingEnvironment environment) {
		final Datatype parent = environment.getSource();
		return (Datatype) parent.get(fieldName);
	}
}