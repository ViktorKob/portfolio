package net.thomas.portfolio.graphql;

import static graphql.Scalars.GraphQLBigDecimal;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLLong;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLList.list;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;
import static java.util.Collections.emptyList;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.DAY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.QUARTER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.WEEK;

import java.util.LinkedList;
import java.util.List;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldDefinition.Builder;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.graphql.fetchers.conversion.FormattedTimeOfEventDataFetcher;
import net.thomas.portfolio.graphql.fetchers.conversion.FormattedTimeOfInterceptionDataFetcher;
import net.thomas.portfolio.graphql.fetchers.conversion.HeadlineDataFetcher;
import net.thomas.portfolio.graphql.fetchers.conversion.SimpleRepresentationDataFetcher;
import net.thomas.portfolio.graphql.fetchers.data_types.DocumentFetcher;
import net.thomas.portfolio.graphql.fetchers.data_types.EntityFetcher;
import net.thomas.portfolio.graphql.fetchers.data_types.IndexableDocumentListFetcher;
import net.thomas.portfolio.graphql.fetchers.data_types.IndexableDocumentSearchFetcher;
import net.thomas.portfolio.graphql.fetchers.data_types.SelectorFetcher;
import net.thomas.portfolio.graphql.fetchers.data_types.SimpleRepresentationFetcher;
import net.thomas.portfolio.graphql.fetchers.data_types.SubTypeArrayFetcher;
import net.thomas.portfolio.graphql.fetchers.data_types.SubTypeFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.GeoLocationFieldDataFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.GeoLocationValueFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.TypeDataFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.UidDataFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.document.RawDataFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.document.TimeOfEventDataFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.document.TimeOfInterceptionDataFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.primitive.DecimalFieldDataFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.primitive.IntegerFieldDataFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.primitive.StringFieldDataFetcher;
import net.thomas.portfolio.graphql.fetchers.fields.primitive.TimestampFieldDataFetcher;
import net.thomas.portfolio.graphql.fetchers.knowledge.SelectorIsKnownFetcher;
import net.thomas.portfolio.graphql.fetchers.knowledge.SelectorKnowledgeFetcher;
import net.thomas.portfolio.graphql.fetchers.references.DocumentReferenceFetcher;
import net.thomas.portfolio.graphql.fetchers.references.ReferenceClassificationsFetcher;
import net.thomas.portfolio.graphql.fetchers.references.ReferenceOriginalIdFetcher;
import net.thomas.portfolio.graphql.fetchers.references.ReferenceSourceFetcher;
import net.thomas.portfolio.graphql.fetchers.statistics.SelectorStatisticsFetcher;
import net.thomas.portfolio.graphql.fetchers.statistics.SelectorStatisticsForPeriodFetcher;
import net.thomas.portfolio.hbase_index.GraphQlUtilities;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Classification;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.RecognitionLevel;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Source;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class GraphQlModelBuilder {
	private String name;
	private String description;
	private HbaseModelAdaptor hbaseModelAdaptor;
	private final GraphQlUtilities utilities;

	public GraphQlModelBuilder(GraphQlUtilities utilities) {
		this.utilities = utilities;
		name = "Unnamed model";
		description = "";
	}

	public GraphQlModelBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public GraphQlModelBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public GraphQlModelBuilder setHbaseModelAdaptor(HbaseModelAdaptor hbaseModelAdaptor) {
		this.hbaseModelAdaptor = hbaseModelAdaptor;
		return this;
	}

	public GraphQLSchema build() {
		final List<GraphQLFieldDefinition> fieldDefinitions = buildFieldDefinitions(hbaseModelAdaptor);
		final GraphQLObjectType query = new GraphQLObjectType(name, description, fieldDefinitions, emptyList());
		final GraphQLSchema.Builder builder = newSchema().query(query);
		return builder.build();
	}

	private List<GraphQLFieldDefinition> buildFieldDefinitions(HbaseModelAdaptor adaptor) {
		final LinkedList<GraphQLFieldDefinition> fields = new LinkedList<>();
		for (final String dataType : adaptor.getDataTypes()) {
			final GraphQLObjectType dataTypeObjectType = buildObjectType(dataType, adaptor);
			List<GraphQLArgument> arguments = uidAnd(new LinkedList<>());
			if (adaptor.isSimpleRepresentable(dataType)) {
				arguments = simpleRepAnd(arguments);
			}
			if (adaptor.isSelector(dataType)) {
				arguments = justificationAnd(dateBoundsAnd(arguments));
			}
			final ModelDataFetcher<?> fetcher = createFetcher(dataType, adaptor);
			fields.add(newFieldDefinition().name(dataType)
				.type(dataTypeObjectType)
				.argument(arguments)
				.dataFetcher(fetcher)
				.build());
		}
		fields.add(newFieldDefinition().name("SelectorStatistics")
			.type(buildSelectorStatisticsType(adaptor))
			.build());
		fields.add(newFieldDefinition().name("PreviousKnowledge")
			.type(buildPreviousKnowledgeType(adaptor))
			.build());
		fields.add(newFieldDefinition().name("DocumentReference")
			.type(buildDocumentReferenceType(adaptor))
			.build());
		fields.add(newFieldDefinition().name("GeoLocation")
			.type(buildGeoLocationType(adaptor))
			.build());
		fields.add(newFieldDefinition().name("ClassificationEnum")
			.type(enumType(Classification.values()))
			.build());
		fields.add(newFieldDefinition().name("SourceEnum")
			.type(enumType(Source.values()))
			.build());
		fields.add(newFieldDefinition().name("RecognitionLevelEnum")
			.type(enumType(RecognitionLevel.values()))
			.build());

		return fields;
	}

	private ModelDataFetcher<?> createFetcher(final String dataType, HbaseModelAdaptor adaptor) {
		if (adaptor.isDocument(dataType)) {
			return new DocumentFetcher(dataType, adaptor);
		} else if (adaptor.isSimpleRepresentable(dataType)) {
			return new SimpleRepresentationFetcher(dataType, adaptor, utilities);
		} else if (adaptor.isSelector(dataType)) {
			return new SelectorFetcher(dataType, adaptor, utilities);
		} else {
			return new EntityFetcher<>(dataType, adaptor);
		}
	}

	private GraphQLObjectType buildObjectType(String dataType, HbaseModelAdaptor adaptor) {
		final GraphQLObjectType.Builder builder = newObject().name(dataType);
		builder.field(newFieldDefinition().name("uid")
			.type(GraphQLString)
			.dataFetcher(new UidDataFetcher(adaptor)));
		builder.field(newFieldDefinition().name("type")
			.type(GraphQLString)
			.dataFetcher(new TypeDataFetcher(adaptor)));
		builder.field(newFieldDefinition().name("headline")
			.type(GraphQLString)
			.dataFetcher(new HeadlineDataFetcher(adaptor)));

		if (adaptor.isSimpleRepresentable(dataType)) {
			builder.description("Simple representable selector");
			builder.field(newFieldDefinition().name("simpleRep")
				.type(GraphQLString)
				.dataFetcher(new SimpleRepresentationDataFetcher(adaptor))
				.build());
		} else if (adaptor.isDocument(dataType)) {
			builder.description("Document");
			builder.field(newFieldDefinition().name("references")
				.type(list(new GraphQLTypeReference("DocumentReference")))
				.dataFetcher(new DocumentReferenceFetcher(adaptor))
				.build());
			builder.field(newFieldDefinition().name("rawData")
				.type(GraphQLString)
				.dataFetcher(new RawDataFetcher(adaptor))
				.build());

			builder.field(newFieldDefinition().name("timeOfEvent")
				.type(GraphQLLong)
				.dataFetcher(new TimeOfEventDataFetcher(adaptor))
				.build());
			builder.field(newFieldDefinition().name("timeOfInterception")
				.type(GraphQLLong)
				.dataFetcher(new TimeOfInterceptionDataFetcher(adaptor))
				.build());
			builder.field(newFieldDefinition().name("formattedTimeOfEvent")
				.type(GraphQLString)
				.argument(format(new LinkedList<>()))
				.dataFetcher(new FormattedTimeOfEventDataFetcher(adaptor, utilities))
				.build());
			builder.field(newFieldDefinition().name("formattedTimeOfInterception")
				.type(GraphQLString)
				.argument(format(new LinkedList<>()))
				.dataFetcher(new FormattedTimeOfInterceptionDataFetcher(adaptor, utilities))
				.build());
		} else if (adaptor.isSelector(dataType)) {
			builder.description("Selector without simple representation");
		} else {
			builder.description("Composition type");
		}

		if (adaptor.isSelector(dataType)) {
			builder.field(newFieldDefinition().name("statistics")
				.type(new GraphQLTypeReference("SelectorStatistics"))
				.dataFetcher(new SelectorStatisticsFetcher(adaptor))
				.build());
			builder.field(newFieldDefinition().name("knowledge")
				.type(new GraphQLTypeReference("PreviousKnowledge"))
				.dataFetcher(new SelectorKnowledgeFetcher(adaptor))
				.build());
			builder.field(newFieldDefinition().name("indexables")
				.type(createInvertedIndexLookupTypeForSelector(dataType, adaptor))
				.argument(pagingAnd(dateBoundsAnd(new LinkedList<>())))
				.dataFetcher(new IndexableDocumentSearchFetcher(adaptor, utilities))
				.build());
		}

		for (final Field field : adaptor.getDataTypeFields(dataType)) {
			if (field instanceof PrimitiveField) {
				builder.field(buildFieldDefinition((PrimitiveField) field, adaptor));
			} else if (field instanceof ReferenceField) {
				builder.field(buildFieldDefinition((ReferenceField) field, adaptor));
			}
		}
		return builder.build();
	}

	private GraphQLFieldDefinition buildFieldDefinition(PrimitiveField field, HbaseModelAdaptor adaptor) {
		final Builder builder = newFieldDefinition().name(field.getName());
		GraphQLOutputType graphQlType = null;
		DataFetcher<?> fetcher = null;
		switch (field.getType()) {
		case DECIMAL:
			fetcher = new DecimalFieldDataFetcher(field.getName(), adaptor);
			graphQlType = GraphQLBigDecimal;
			break;
		case INTEGER:
			fetcher = new IntegerFieldDataFetcher(field.getName(), adaptor);
			graphQlType = GraphQLLong;
			break;
		case TIMESTAMP:
			fetcher = new TimestampFieldDataFetcher(field.getName(), adaptor, utilities);
			graphQlType = GraphQLString;
			break;
		case GEO_LOCATION:
			fetcher = new GeoLocationFieldDataFetcher(field.getName(), adaptor);
			graphQlType = new GraphQLTypeReference("GeoLocation");
			break;
		case STRING:
		default:
			fetcher = new StringFieldDataFetcher(field.getName(), adaptor);
			graphQlType = GraphQLString;
			break;
		}

		if (field.isArray()) {
			builder.type(list(graphQlType))
				.dataFetcher(fetcher);
		} else {
			builder.type(graphQlType)
				.dataFetcher(fetcher);
		}
		return builder.build();
	}

	private GraphQLFieldDefinition buildFieldDefinition(ReferenceField field, HbaseModelAdaptor adaptor) {
		final Builder builder = newFieldDefinition().name(field.getName());
		final GraphQLOutputType graphQlType = new GraphQLTypeReference(field.getType());
		if (field.isArray()) {
			builder.type(list(graphQlType))
				.dataFetcher(new SubTypeArrayFetcher(field.getName(), adaptor));
		} else {
			builder.type(graphQlType)
				.dataFetcher(new SubTypeFetcher(field.getName(), adaptor));
		}
		return builder.build();
	}

	private List<GraphQLArgument> uidAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("uid")
			.description("DataType UID")
			.type(GraphQLString)
			.build());
		return arguments;
	}

	private List<GraphQLArgument> simpleRepAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("simpleRep")
			.description("Selector simple representation")
			.type(GraphQLString)
			.build());
		return arguments;
	}

	private List<GraphQLArgument> justificationAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("justification")
			.description("Justification for search")
			.type(GraphQLString)
			.build());
		return arguments;
	}

	private List<GraphQLArgument> format(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("format")
			.description("Date rendering format; 'dateOnly' to only render year-month-date")
			.type(GraphQLString)
			.build());
		return arguments;
	}

	private List<GraphQLArgument> pagingAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("offset")
			.description("First index to include")
			.type(GraphQLInt)
			.defaultValue(0)
			.build());
		arguments.add(newArgument().name("limit")
			.description("Number of elements to include")
			.type(GraphQLInt)
			.defaultValue(20)
			.build());
		return arguments;
	}

	private List<GraphQLArgument> dateBoundsAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("after")
			.description("Lower bound unix timestamp")
			.type(GraphQLLong)
			.build());
		arguments.add(newArgument().name("before")
			.description("Upper bound unix timestamp")
			.type(GraphQLLong)
			.build());
		arguments.add(newArgument().name("afterDate")
			.description("Lower bound formatted date, e.g. '2017-11-23' or '2017-11-23 12:34:56 +0200'")
			.type(GraphQLString)
			.build());
		arguments.add(newArgument().name("beforeDate")
			.description("Upper bound formatted date, e.g. '2017-11-23' or '2017-11-23 12:34:56 +0200'")
			.type(GraphQLString)
			.build());
		return arguments;
	}

	private GraphQLObjectType createInvertedIndexLookupTypeForSelector(String dataType, HbaseModelAdaptor adaptor) {
		final GraphQLObjectType.Builder builder = newObject().name(dataType + "Indexables")
			.description("Indexables for specific selector");
		for (final Indexable indexable : adaptor.getIndexables(dataType)) {
			builder.field(newFieldDefinition().name(indexable.path + "_" + indexable.documentType)
				.type(list(new GraphQLTypeReference(indexable.documentType)))
				.dataFetcher(new IndexableDocumentListFetcher(indexable, adaptor))
				.build());
		}
		return builder.build();
	}

	private GraphQLOutputType buildSelectorStatisticsType(HbaseModelAdaptor adaptor) {
		final GraphQLObjectType.Builder builder = newObject().name("SelectorStatistics")
			.description("Statistics for specific selector over time");
		builder.field(newFieldDefinition().name("dayTotal")
			.type(GraphQLLong)
			.dataFetcher(new SelectorStatisticsForPeriodFetcher(DAY, adaptor))
			.build());
		builder.field(newFieldDefinition().name("weekTotal")
			.type(GraphQLLong)
			.dataFetcher(new SelectorStatisticsForPeriodFetcher(WEEK, adaptor))
			.build());
		builder.field(newFieldDefinition().name("quarterTotal")
			.type(GraphQLLong)
			.dataFetcher(new SelectorStatisticsForPeriodFetcher(QUARTER, adaptor))
			.build());
		builder.field(newFieldDefinition().name("infinityTotal")
			.type(GraphQLLong)
			.dataFetcher(new SelectorStatisticsForPeriodFetcher(INFINITY, adaptor))
			.build());
		return builder.build();
	}

	private GraphQLObjectType buildPreviousKnowledgeType(HbaseModelAdaptor adaptor) {
		final GraphQLObjectType.Builder builder = newObject().name("PreviousKnowledge")
			.description("Previous knowledge about selector");
		builder.field(newFieldDefinition().name("isKnown")
			.type(new GraphQLTypeReference("RecognitionLevelEnum"))
			.dataFetcher(new SelectorIsKnownFetcher(adaptor))
			.build());
		builder.field(newFieldDefinition().name("isDanish")
			.type(new GraphQLTypeReference("RecognitionLevelEnum"))
			.dataFetcher(new SelectorIsKnownFetcher(adaptor))
			.build());
		return builder.build();
	}

	private GraphQLOutputType buildDocumentReferenceType(HbaseModelAdaptor adaptor) {
		final GraphQLObjectType.Builder builder = newObject().name("DocumentReference")
			.description("Source reference for specific document");
		builder.field(newFieldDefinition().name("originalId")
			.type(GraphQLString)
			.dataFetcher(new ReferenceOriginalIdFetcher(adaptor))
			.build());
		builder.field(newFieldDefinition().name("classifications")
			.type(list(new GraphQLTypeReference("ClassificationEnum")))
			.dataFetcher(new ReferenceClassificationsFetcher(adaptor))
			.build());
		builder.field(newFieldDefinition().name("source")
			.type(new GraphQLTypeReference("SourceEnum"))
			.dataFetcher(new ReferenceSourceFetcher(adaptor))
			.build());
		return builder.build();
	}

	private GraphQLEnumType enumType(Enum<?>[] values) {
		final List<GraphQLEnumValueDefinition> enumValues = new LinkedList<>();
		String name = null;
		for (final Enum<?> value : values) {
			name = value.getClass()
				.getSimpleName();
			enumValues.add(new GraphQLEnumValueDefinition(value.name(), value.name() + " in Enum " + name, value));
		}
		return new GraphQLEnumType(name + "Enum", "Mapping of Enum " + name + " to GraphQL", enumValues);
	}

	private GraphQLOutputType buildGeoLocationType(HbaseModelAdaptor adaptor) {
		final GraphQLObjectType.Builder builder = newObject().name("GeoLocation")
			.description("Location on Earth in Longitude and Latitude");
		builder.field(newFieldDefinition().name("longitude")
			.type(GraphQLBigDecimal)
			.dataFetcher(new GeoLocationValueFetcher("longitude", adaptor))
			.build());
		builder.field(newFieldDefinition().name("latitude")
			.type(GraphQLBigDecimal)
			.dataFetcher(new GeoLocationValueFetcher("latitude", adaptor))
			.build());
		return builder.build();
	}
}
