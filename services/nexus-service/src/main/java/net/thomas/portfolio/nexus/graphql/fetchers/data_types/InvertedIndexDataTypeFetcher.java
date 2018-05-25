package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import static java.util.Collections.singletonList;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

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
			return singletonList(adaptors.getDataType(new DataTypeId(selectorType, uid.toString())));
		}
		final Object simpleRepresentation = environment.getArgument("simpleRep");
		if (simpleRepresentation != null) {
			final DataTypeId id = adaptors.getIdFromSimpleRep(selectorType, simpleRepresentation.toString());
			return singletonList(adaptors.getDataType(id));
		}
		return null;
	}
}