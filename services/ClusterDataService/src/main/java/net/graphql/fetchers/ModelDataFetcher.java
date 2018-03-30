package net.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public abstract class ModelDataFetcher<RESULT_TYPE> implements DataFetcher<RESULT_TYPE> {

	protected final ModelAdaptor adaptor;
	private final long fakeResponseDelay;

	public ModelDataFetcher(ModelAdaptor adaptor, long fakeResponseDelay) {
		this.adaptor = adaptor;
		this.fakeResponseDelay = fakeResponseDelay;
	}

	@Override
	public final RESULT_TYPE get(DataFetchingEnvironment environment) {
		if (fakeResponseDelay > 0) {
			try {
				Thread.sleep(fakeResponseDelay);
			} catch (final InterruptedException e) {
				// Ignored
			}
		}
		return _get(environment);
	}

	public abstract RESULT_TYPE _get(DataFetchingEnvironment environment);
}
