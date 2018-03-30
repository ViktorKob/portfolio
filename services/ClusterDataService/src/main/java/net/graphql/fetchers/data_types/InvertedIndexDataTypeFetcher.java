package net.graphql.fetchers.data_types;

import java.util.Collections;
import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;

public class InvertedIndexDataTypeFetcher extends ModelDataFetcher<List<DataType>> {

	private final String selectorType;

	public InvertedIndexDataTypeFetcher(String selectorType, ModelAdaptor adaptor) {
		super(adaptor, 50);
		this.selectorType = selectorType;
	}

	@Override
	public List<DataType> _get(DataFetchingEnvironment environment) {
		final Object uid = environment.getArgument("uid");
		if (uid != null) {
			return Collections.singletonList(adaptor.getDataTypeByUid(selectorType, uid.toString()));
		}
		final Object simpleRepresentation = environment.getArgument("simpleRep");
		if (simpleRepresentation != null) {
			return Collections.singletonList(adaptor.getDataTypeBySimpleRepresentation(selectorType, simpleRepresentation.toString()));
		}
		return null;
	}
}