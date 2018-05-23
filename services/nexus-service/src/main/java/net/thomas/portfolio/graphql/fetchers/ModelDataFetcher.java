package net.thomas.portfolio.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public abstract class ModelDataFetcher<RESULT_TYPE> implements DataFetcher<RESULT_TYPE> {

	protected final HbaseModelAdaptor adaptor;
	private final long fakeResponseDelay;

	public ModelDataFetcher(HbaseModelAdaptor adaptor, long fakeResponseDelay) {
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
