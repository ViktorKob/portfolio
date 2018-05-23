package net.thomas.portfolio.graphql.fetchers.data_types;

import java.util.Collections;
import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class InvertedIndexDataTypeFetcher extends ModelDataFetcher<List<DataType>> {

	private final String selectorType;

	public InvertedIndexDataTypeFetcher(String selectorType, Adaptors adaptors) {
		super(adaptors/* , 0 */);
		this.selectorType = selectorType;
	}

	@Override
	public List<DataType> _get(DataFetchingEnvironment environment) {
		final Object uid = environment.getArgument("uid");
		if (uid != null) {
			return Collections.singletonList(adaptors.getDataTypeByUid(selectorType, uid.toString()));
		}
		final Object simpleRepresentation = environment.getArgument("simpleRep");
		if (simpleRepresentation != null) {
			return Collections.singletonList(adaptors.getDataTypeBySimpleRepresentation(selectorType, simpleRepresentation.toString()));
		}
		return null;
	}
}