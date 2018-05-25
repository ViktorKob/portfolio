package net.thomas.portfolio.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public abstract class ModelDataFetcher<RESULT_TYPE> implements DataFetcher<RESULT_TYPE> {

	protected final Adaptors adaptors;
	// private final long fakeResponseDelay;

	public ModelDataFetcher(Adaptors adaptors/* , long fakeResponseDelay */) {
		this.adaptors = adaptors;
		// this.fakeResponseDelay = fakeResponseDelay;
	}

	@Override
	public final RESULT_TYPE get(DataFetchingEnvironment environment) {
		// if (fakeResponseDelay > 0) {
		// try {
		// Thread.sleep(fakeResponseDelay);
		// } catch (final InterruptedException e) {
		// // Ignored
		// }
		// }
		return _get(environment);
	}

	public abstract RESULT_TYPE _get(DataFetchingEnvironment environment);

	protected DataType extractOrFetchDataType(DataFetchingEnvironment environment) {
		final Object entity = environment.getSource();
		if (entity instanceof DataType) {
			return (DataType) entity;
		} else if (entity instanceof DocumentInfo) {
			final DataTypeId id = ((DocumentInfo) entity).getId();
			return adaptors.getDataType(id);
		}
		throw new RuntimeException("Unable to convert data type of type " + entity.getClass()
			.getSimpleName());
	}
}
