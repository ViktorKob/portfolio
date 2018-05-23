package net.thomas.portfolio.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class HeadlineDataFetcher extends ModelDataFetcher<String> {

	// private final Renderer<String> renderer;

	public HeadlineDataFetcher(HbaseModelAdaptor adaptor) {
		super(adaptor, 0);
		// renderer = adaptor.getHeadlineRenderers();
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		// final DataType entity = (DataType) environment.getSource();
		// return renderer.render(entity);
		return null;
	}
}