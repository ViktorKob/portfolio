package net.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;
import net.model.meta_data.Renderer;

public class HeadlineDataFetcher extends ModelDataFetcher<String> {

	private final Renderer<String> renderer;

	public HeadlineDataFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
		renderer = adaptor.getHeadlineRenderers();
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return renderer.render(entity);
	}
}