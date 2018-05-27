package net.thomas.portfolio.nexus.graphql;

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
import static java.util.stream.Collectors.joining;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.DAY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.QUARTER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.WEEK;

import java.util.Collection;
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
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.conversion.FormattedTimeOfEventDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.conversion.FormattedTimeOfInterceptionDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.conversion.HeadlineDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.conversion.HtmlDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.conversion.SimpleRepresentationDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_types.DocumentFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_types.DocumentListFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_types.EntityFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_types.IndexableDocumentSearchFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_types.SelectorFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_types.SimpleRepresentationFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_types.SubTypeArrayFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_types.SubTypeFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.GeoLocationFieldDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.GeoLocationValueFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.TypeDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.UidDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.document.RawDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.document.TimeOfEventDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.document.TimeOfInterceptionDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.primitive.DecimalFieldDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.primitive.IntegerFieldDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.primitive.StringFieldDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.fields.primitive.TimestampFieldDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.knowledge.SelectorAliasFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.knowledge.SelectorIsDanishFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.knowledge.SelectorIsKnownFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.knowledge.SelectorKnowledgeFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.references.DocumentReferenceFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.references.ReferenceClassificationsFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.references.ReferenceOriginalIdFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.references.ReferenceSourceFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.statistics.SelectorStatisticsFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.statistics.SelectorStatisticsForPeriodFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.adaptors.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.LegalAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.RenderingAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.UsageAdaptor;
import net.thomas.portfolio.shared_objects.analytics.RecognitionLevel;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Classification;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Source;

public class GraphQlModelBuilder {
	private AnalyticsAdaptor analyticsAdaptor;
	private HbaseIndexModelAdaptor hbaseModelAdaptor;
	private LegalAdaptor legalAdaptor;
	private RenderingAdaptor renderingAdaptor;
	private UsageAdaptor usageAdaptor;

	public GraphQlModelBuilder() {
	}

	public GraphQlModelBuilder setAnalyticsAdaptor(AnalyticsAdaptor analyticsAdaptor) {
		this.analyticsAdaptor = analyticsAdaptor;
		return this;
	}

	public GraphQlModelBuilder setHbaseModelAdaptor(HbaseIndexModelAdaptor hbaseModelAdaptor) {
		this.hbaseModelAdaptor = hbaseModelAdaptor;
		return this;
	}

	public GraphQlModelBuilder setLegalAdaptor(LegalAdaptor legalAdaptor) {
		this.legalAdaptor = legalAdaptor;
		return this;
	}

	public GraphQlModelBuilder setRenderingAdaptor(RenderingAdaptor renderingAdaptor) {
		this.renderingAdaptor = renderingAdaptor;
		return this;
	}

	public GraphQlModelBuilder setUsageAdaptor(UsageAdaptor usageAdaptor) {
		this.usageAdaptor = usageAdaptor;
		return this;
	}

	public GraphQLSchema build() {
		final Adaptors adaptors = new Adaptors(analyticsAdaptor, hbaseModelAdaptor, legalAdaptor, renderingAdaptor, usageAdaptor);
		final List<GraphQLFieldDefinition> fieldDefinitions = buildFieldDefinitions(adaptors);
		final GraphQLObjectType query = new GraphQLObjectType("NexusModel", "Model enabling use of all sub-services as one data structure", fieldDefinitions,
				emptyList());
		final GraphQLSchema.Builder builder = newSchema().query(query);
		return builder.build();
	}

	private List<GraphQLFieldDefinition> buildFieldDefinitions(Adaptors adaptors) {
		final LinkedList<GraphQLFieldDefinition> fields = new LinkedList<>();
		for (final String dataType : adaptors.getDataTypes()) {
			final GraphQLObjectType dataTypeObjectType = buildObjectType(dataType, adaptors);
			List<GraphQLArgument> arguments = uidAnd(new LinkedList<>());
			if (adaptors.isSimpleRepresentable(dataType)) {
				arguments = simpleRepAnd(arguments);
			}
			if (adaptors.isSelector(dataType)) {
				arguments = justificationAnd(dateBoundsAnd(arguments));
			}
			final ModelDataFetcher<?> fetcher = createFetcher(dataType, adaptors);
			fields.add(newFieldDefinition().name(dataType)
				.type(dataTypeObjectType)
				.argument(arguments)
				.dataFetcher(fetcher)
				.build());
		}
		fields.add(newFieldDefinition().name("SelectorStatistics")
			.type(buildSelectorStatisticsType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("PreviousKnowledge")
			.type(buildPreviousKnowledgeType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("DocumentReference")
			.type(buildDocumentReferenceType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("GeoLocation")
			.type(buildGeoLocationType(adaptors))
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

	private ModelDataFetcher<?> createFetcher(final String dataType, Adaptors adaptors) {
		if (adaptors.isDocument(dataType)) {
			return new DocumentFetcher(dataType, adaptors);
		} else if (adaptors.isSimpleRepresentable(dataType)) {
			return new SimpleRepresentationFetcher(dataType, adaptors);
		} else if (adaptors.isSelector(dataType)) {
			return new SelectorFetcher(dataType, adaptors);
		} else {
			return new EntityFetcher<>(dataType, adaptors);
		}
	}

	private GraphQLObjectType buildObjectType(String dataType, Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = newObject().name(dataType);
		builder.field(newFieldDefinition().name("uid")
			.type(GraphQLString)
			.dataFetcher(new UidDataFetcher(adaptors)));
		builder.field(newFieldDefinition().name("type")
			.type(GraphQLString)
			.dataFetcher(new TypeDataFetcher(adaptors)));
		builder.field(newFieldDefinition().name("headline")
			.type(GraphQLString)
			.dataFetcher(new HeadlineDataFetcher(adaptors)));
		builder.field(newFieldDefinition().name("html")
			.type(GraphQLString)
			.dataFetcher(new HtmlDataFetcher(adaptors)));

		if (adaptors.isSimpleRepresentable(dataType)) {
			builder.description("Simple representable selector");
			builder.field(newFieldDefinition().name("simpleRep")
				.type(GraphQLString)
				.dataFetcher(new SimpleRepresentationDataFetcher(adaptors))
				.build());
		} else if (adaptors.isDocument(dataType)) {
			builder.description("Document");
			builder.field(newFieldDefinition().name("references")
				.type(list(new GraphQLTypeReference("DocumentReference")))
				.dataFetcher(new DocumentReferenceFetcher(adaptors))
				.build());
			builder.field(newFieldDefinition().name("rawData")
				.type(GraphQLString)
				.dataFetcher(new RawDataFetcher(adaptors))
				.build());

			builder.field(newFieldDefinition().name("timeOfEvent")
				.type(GraphQLLong)
				.dataFetcher(new TimeOfEventDataFetcher(adaptors))
				.build());
			builder.field(newFieldDefinition().name("timeOfInterception")
				.type(GraphQLLong)
				.dataFetcher(new TimeOfInterceptionDataFetcher(adaptors))
				.build());
			builder.field(newFieldDefinition().name("formattedTimeOfEvent")
				.type(GraphQLString)
				.argument(format(new LinkedList<>()))
				.dataFetcher(new FormattedTimeOfEventDataFetcher(adaptors))
				.build());
			builder.field(newFieldDefinition().name("formattedTimeOfInterception")
				.type(GraphQLString)
				.argument(format(new LinkedList<>()))
				.dataFetcher(new FormattedTimeOfInterceptionDataFetcher(adaptors))
				.build());
		} else if (adaptors.isSelector(dataType)) {
			builder.description("Selector without simple representation");
		} else {
			builder.description("Composition type");
		}

		if (adaptors.isSelector(dataType)) {
			builder.field(newFieldDefinition().name("statistics")
				.type(new GraphQLTypeReference("SelectorStatistics"))
				.dataFetcher(new SelectorStatisticsFetcher(adaptors))
				.build());
			builder.field(newFieldDefinition().name("knowledge")
				.type(new GraphQLTypeReference("PreviousKnowledge"))
				.dataFetcher(new SelectorKnowledgeFetcher(adaptors))
				.build());
			List<GraphQLArgument> arguments = pagingAnd(dateBoundsAnd(new LinkedList<>()));
			arguments = relationsAnd(arguments, adaptors.getIndexedDocumentTypes(dataType));
			arguments = documentTypesAnd(arguments, adaptors.getIndexedRelationTypes(dataType));
			arguments = justificationAnd(arguments);
			builder.field(newFieldDefinition().name("events")
				.type(createInvertedIndexLookupTypeForSelector(dataType, adaptors))
				.argument(arguments)
				.dataFetcher(new IndexableDocumentSearchFetcher(adaptors))
				.build());
		}

		for (final Field field : adaptors.getDataTypeFields(dataType)) {
			if (field instanceof PrimitiveField) {
				builder.field(buildFieldDefinition((PrimitiveField) field, adaptors));
			} else if (field instanceof ReferenceField) {
				builder.field(buildFieldDefinition((ReferenceField) field, adaptors));
			}
		}
		return builder.build();
	}

	private GraphQLFieldDefinition buildFieldDefinition(PrimitiveField field, Adaptors adaptors) {
		final Builder builder = newFieldDefinition().name(field.getName());
		GraphQLOutputType graphQlType = null;
		DataFetcher<?> fetcher = null;
		switch (field.getType()) {
		case DECIMAL:
			fetcher = new DecimalFieldDataFetcher(field.getName(), adaptors);
			graphQlType = GraphQLBigDecimal;
			break;
		case INTEGER:
			fetcher = new IntegerFieldDataFetcher(field.getName(), adaptors);
			graphQlType = GraphQLLong;
			break;
		case TIMESTAMP:
			fetcher = new TimestampFieldDataFetcher(field.getName(), adaptors);
			graphQlType = GraphQLString;
			break;
		case GEO_LOCATION:
			fetcher = new GeoLocationFieldDataFetcher(field.getName(), adaptors);
			graphQlType = new GraphQLTypeReference("GeoLocation");
			break;
		case STRING:
		default:
			fetcher = new StringFieldDataFetcher(field.getName(), adaptors);
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

	private GraphQLFieldDefinition buildFieldDefinition(ReferenceField field, Adaptors adaptors) {
		final Builder builder = newFieldDefinition().name(field.getName());
		final GraphQLOutputType graphQlType = new GraphQLTypeReference(field.getType());
		if (field.isArray()) {
			builder.type(list(graphQlType))
				.dataFetcher(new SubTypeArrayFetcher(field.getName(), adaptors));
		} else {
			builder.type(graphQlType)
				.dataFetcher(new SubTypeFetcher(field.getName(), adaptors));
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
			.description("Justification for executing query")
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

	private List<GraphQLArgument> documentTypesAnd(List<GraphQLArgument> arguments, Collection<String> documentTypes) {
		final String documentTypeList = "[ " + documentTypes.stream()
			.sorted()
			.collect(joining(", ")) + " ]";
		arguments.add(newArgument().name("documentTypes")
			.description("Document types that should be included in the response (from the set " + documentTypeList + " )")
			.type(list(GraphQLString))
			.build());
		return arguments;
	}

	private List<GraphQLArgument> relationsAnd(List<GraphQLArgument> arguments, Collection<String> relationTypes) {
		final String relationTypeList = "[ " + relationTypes.stream()
			.sorted()
			.collect(joining(", ")) + " ]";
		arguments.add(newArgument().name("relations")
			.description("Relation types that should be included in the response (from the set " + relationTypeList + " )")
			.type(list(GraphQLString))
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

	private GraphQLObjectType createInvertedIndexLookupTypeForSelector(String dataType, Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = newObject().name(dataType + "_documentTypes")
			.description("Document types that can result from a search on this type");
		// TODO[Thomas]: Change when Interfaces are added in GraphQL model
		for (final String documentType : adaptors.getIndexedDocumentTypes(dataType)) {
			builder.field(newFieldDefinition().name(documentType)
				.type(list(new GraphQLTypeReference(documentType)))
				.dataFetcher(new DocumentListFetcher(documentType, adaptors))
				.build());
		}
		return builder.build();
	}

	private GraphQLOutputType buildSelectorStatisticsType(Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = newObject().name("SelectorStatistics")
			.description("Statistics for specific selector over time");
		builder.field(newFieldDefinition().name("dayTotal")
			.type(GraphQLLong)
			.dataFetcher(new SelectorStatisticsForPeriodFetcher(DAY, adaptors))
			.build());
		builder.field(newFieldDefinition().name("weekTotal")
			.type(GraphQLLong)
			.dataFetcher(new SelectorStatisticsForPeriodFetcher(WEEK, adaptors))
			.build());
		builder.field(newFieldDefinition().name("quarterTotal")
			.type(GraphQLLong)
			.dataFetcher(new SelectorStatisticsForPeriodFetcher(QUARTER, adaptors))
			.build());
		builder.field(newFieldDefinition().name("infinityTotal")
			.type(GraphQLLong)
			.dataFetcher(new SelectorStatisticsForPeriodFetcher(INFINITY, adaptors))
			.build());
		return builder.build();
	}

	private GraphQLObjectType buildPreviousKnowledgeType(Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = newObject().name("PreviousKnowledge")
			.description("Previous knowledge about selector");
		builder.field(newFieldDefinition().name("alias")
			.description("Alternative name for selector")
			.type(GraphQLString)
			.dataFetcher(new SelectorAliasFetcher(adaptors))
			.build());
		builder.field(newFieldDefinition().name("isKnown")
			.description("Whether we are already interested in this selector")
			.type(new GraphQLTypeReference("RecognitionLevelEnum"))
			.dataFetcher(new SelectorIsKnownFetcher(adaptors))
			.build());
		builder.field(newFieldDefinition().name("isDanish")
			.description("Whether this selector is known to belong to a danish citizen")
			.type(new GraphQLTypeReference("RecognitionLevelEnum"))
			.dataFetcher(new SelectorIsDanishFetcher(adaptors))
			.build());
		return builder.build();
	}

	private GraphQLOutputType buildDocumentReferenceType(Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = newObject().name("DocumentReference")
			.description("Source reference for specific document");
		builder.field(newFieldDefinition().name("originalId")
			.type(GraphQLString)
			.dataFetcher(new ReferenceOriginalIdFetcher(adaptors))
			.build());
		builder.field(newFieldDefinition().name("classifications")
			.type(list(new GraphQLTypeReference("ClassificationEnum")))
			.dataFetcher(new ReferenceClassificationsFetcher(adaptors))
			.build());
		builder.field(newFieldDefinition().name("source")
			.type(new GraphQLTypeReference("SourceEnum"))
			.dataFetcher(new ReferenceSourceFetcher(adaptors))
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

	private GraphQLOutputType buildGeoLocationType(Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = newObject().name("GeoLocation")
			.description("Location on Earth in Longitude and Latitude");
		builder.field(newFieldDefinition().name("longitude")
			.type(GraphQLBigDecimal)
			.dataFetcher(new GeoLocationValueFetcher("longitude", adaptors))
			.build());
		builder.field(newFieldDefinition().name("latitude")
			.type(GraphQLBigDecimal)
			.dataFetcher(new GeoLocationValueFetcher("latitude", adaptors))
			.build());
		return builder.build();
	}
}
