package net.thomas.portfolio.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class SimpleRepresentationDataFetcher extends ModelDataFetcher<Object> {
	// private final Renderer<String> renderer;

	public SimpleRepresentationDataFetcher(HbaseModelAdaptor adaptor) {
		super(adaptor, 0);
		// renderer = adaptor.getSimpleRepresentationRenderers();
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		// final DataType entity = (DataType) environment.getSource();
		// return renderer.render(entity);
		return null;
	}
}