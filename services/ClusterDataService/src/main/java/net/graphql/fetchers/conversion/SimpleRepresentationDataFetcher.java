package net.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;
import net.model.meta_data.Renderer;

public class SimpleRepresentationDataFetcher extends ModelDataFetcher<Object> {
	private final Renderer<String> renderer;

	public SimpleRepresentationDataFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
		renderer = adaptor.getSimpleRepresentationRenderers();
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return renderer.render(entity);
	}
}