package net.thomas.portfolio.nexus.graphql;

import static graphql.Scalars.GraphQLBigDecimal;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLLong;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLList.list;
import static graphql.schema.GraphQLNonNull.nonNull;
import static graphql.schema.GraphQLObjectType.newObject;
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
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
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
import net.thomas.portfolio.nexus.graphql.fetchers.knowledge.SelectorIsKnownFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.knowledge.SelectorIsRestrictedFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.knowledge.SelectorKnowledgeFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.references.DocumentReferencesFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.references.ReferenceClassificationsFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.references.ReferenceOriginalIdFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.references.ReferenceSourceFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.statistics.SelectorStatisticsFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.statistics.SelectorStatisticsForPeriodFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.usage_data.FormattedTimeOfActivityFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.usage_data.UsageActivityItemsFetcher;
import net.thomas.portfolio.nexus.graphql.resolvers.DataTypeResolver;
import net.thomas.portfolio.nexus.graphql.resolvers.DocumentResolver;
import net.thomas.portfolio.nexus.graphql.resolvers.SelectorResolver;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Classification;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Source;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityItem;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

public class GraphQlMutationModelBuilder {

	private Adaptors adaptors;

	public GraphQlMutationModelBuilder() {
	}

	public GraphQlMutationModelBuilder setAdaptors(Adaptors adaptors) {
		this.adaptors = adaptors;
		return this;
	}

	public GraphQLObjectType build() {
		final List<GraphQLFieldDefinition> mutationFieldDefinitions = buildMutationFieldDefinitions(adaptors);
		return new GraphQLObjectType("NexusMutationModel", "Model enabling modifications of all relevant sub-services as one data structure",
				mutationFieldDefinitions, emptyList());
	}

	private List<GraphQLFieldDefinition> buildMutationFieldDefinitions(Adaptors adaptors) {
		final LinkedList<GraphQLFieldDefinition> fields = new LinkedList<>();
		for (final String dataType : adaptors.getDataTypes()) {
			final GraphQLObjectType dataTypeObjectType = buildObjectType(dataType, adaptors);
			List<GraphQLArgument> arguments = uidAnd(new LinkedList<>());
			if (adaptors.isSimpleRepresentable(dataType)) {
				arguments = simpleRepAnd(arguments);
			}
			final ModelDataFetcher<?> fetcher = createFetcher(dataType, adaptors);
			fields.add(newFieldDefinition().name(dataType)
				.description("Fields and functions in the " + dataType + " type")
				.type(dataTypeObjectType)
				.argument(arguments)
				.dataFetcher(fetcher)
				.build());
		}
		fields.add(newFieldDefinition().name("DataType")
			.description("Interface for the various document types (" + buildPresentationListFromCollection(adaptors.getDataTypes()) + ")")
			.type(buildDataTypeInterfaceType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("Document")
			.description("Interface for the various document types (" + buildPresentationListFromCollection(adaptors.getDocumentTypes()) + ")")
			.type(buildDocumentInterfaceType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("Selector")
			.description("Interface for the various document types (" + buildPresentationListFromCollection(adaptors.getSelectorTypes()) + ")")
			.type(buildSelectorInterfaceType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("SelectorStatistics")
			.description("Various statistics for specific selectors")
			.type(buildSelectorStatisticsType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("Knowledge")
			.description("Existing in-house knowledge about selectors")
			.type(buildKnowledgeType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("DocumentReference")
			.description("Reference information element describing how a document was obtained and restrictions on its usage")
			.type(buildDocumentReferenceType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("UsageActivityItem")
			.description("Activity by specific user on a specific document at a specific point in time")
			.type(buildUsageActivityItemType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("GeoLocation")
			.description("Longitude and lattitude for position related to selectors or documents")
			.type(buildGeoLocationType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("ClassificationEnum")
			.description("Possible classifications for documents")
			.type(enumType(Classification.values()))
			.build());
		fields.add(newFieldDefinition().name("SourceEnum")
			.description("Enumeration of the various sources for the indexed documents")
			.type(enumType(Source.values()))
			.build());
		fields.add(newFieldDefinition().name("ConfidenceLevelEnum")
			.description("Confidence level for various properties for selectors")
			.type(enumType(ConfidenceLevel.values()))
			.build());
		fields.add(newFieldDefinition().name("UsageActivityTypeEnum")
			.description("Confidence level for various properties for selectors")
			.type(enumType(UsageActivityType.values()))
			.build());
		return fields;
	}

	private List<GraphQLFieldDefinition> buildQueryFieldDefinitions(Adaptors adaptors) {
		final LinkedList<GraphQLFieldDefinition> fields = new LinkedList<>();
		for (final String dataType : adaptors.getDataTypes()) {
			final GraphQLObjectType dataTypeObjectType = buildObjectType(dataType, adaptors);
			List<GraphQLArgument> arguments = uidAnd(new LinkedList<>());
			if (adaptors.isSimpleRepresentable(dataType)) {
				arguments = simpleRepAnd(arguments);
			}
			final ModelDataFetcher<?> fetcher = createFetcher(dataType, adaptors);
			fields.add(newFieldDefinition().name(dataType)
				.description("Fields and functions in the " + dataType + " type")
				.type(dataTypeObjectType)
				.argument(arguments)
				.dataFetcher(fetcher)
				.build());
		}
		fields.add(newFieldDefinition().name("Document")
			.description("Interface for the various document types (" + buildPresentationListFromCollection(adaptors.getDocumentTypes()) + ")")
			.type(buildDocumentInterfaceType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("UsageActivityItem")
			.description("Activity by specific user on a specific document at a specific point in time")
			.type(buildUsageActivityItemType(adaptors))
			.build());
		fields.add(newFieldDefinition().name("UsageActivityTypeEnum")
			.description("Confidence level for various properties for selectors")
			.type(enumType(UsageActivityType.values()))
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
		builder.withInterface(new GraphQLTypeReference("DataType"));
		builder.field(createUidField(adaptors));
		builder.field(createTypeField(adaptors));
		builder.field(createHeadlineField(adaptors));
		builder.field(createHtmlField(adaptors));

		if (adaptors.isSimpleRepresentable(dataType)) {
			builder.description("A selector with a simple, easily recognizable representation like for instance an email address or a name");
			builder.field(createSimpleRepresentationField(adaptors));
		} else if (adaptors.isDocument(dataType)) {
			builder.description("A document describing an event that occurred at some point in time");
			builder.withInterface(new GraphQLTypeReference("Document"));
			builder.field(createReferencesField(adaptors));
			builder.field(createUsageDataItemsField(adaptors));
			builder.field(createRawDataField(adaptors));
			builder.field(createTimeOfEventField(adaptors));
			builder.field(createTimeOfInterceptionField(adaptors));
			builder.field(createFormattedTimeOfEventField(adaptors));
			builder.field(createFormattedTimeOfInterceptionField(adaptors));
		} else if (adaptors.isSelector(dataType)) {
			builder.description("A selector without a simple, easily recognizable representation like for instance a geo-tile");
		} else {
			builder.description("A composition type that show a relation between multiple selectors, without being a selector itself");
		}

		if (adaptors.isSelector(dataType)) {
			builder.withInterface(new GraphQLTypeReference("Selector"));
			builder.field(createStatisticsField(adaptors));
			builder.field(createKnowledgeField(adaptors));
			builder.field(createInvertedIndexLookupField(dataType, adaptors));
		}

		for (final Field field : adaptors.getDataTypeFields(dataType)) {
			if (field instanceof PrimitiveField) {
				builder.field(buildFieldDefinition((PrimitiveField) field, dataType, adaptors));
			} else if (field instanceof ReferenceField) {
				builder.field(buildFieldDefinition((ReferenceField) field, dataType, adaptors));
			}
		}
		return builder.build();
	}

	private GraphQLFieldDefinition buildFieldDefinition(PrimitiveField field, String parentType, Adaptors adaptors) {
		final Builder builder = newFieldDefinition().name(field.getName());
		GraphQLOutputType graphQlType = null;
		DataFetcher<?> fetcher = null;
		String description = "";
		switch (field.getType()) {
		case DECIMAL:
			fetcher = new DecimalFieldDataFetcher(field.getName(), adaptors);
			graphQlType = GraphQLBigDecimal;
			description = buildDescription("Decimal field", field, parentType);
			break;
		case INTEGER:
			fetcher = new IntegerFieldDataFetcher(field.getName(), adaptors);
			graphQlType = GraphQLLong;
			description = buildDescription("Integer field", field, parentType);
			break;
		case TIMESTAMP:
			fetcher = new TimestampFieldDataFetcher(field.getName(), adaptors);
			graphQlType = GraphQLString;
			description = buildDescription("Timestamp", field, parentType);
			break;
		case GEO_LOCATION:
			fetcher = new GeoLocationFieldDataFetcher(field.getName(), adaptors);
			graphQlType = new GraphQLTypeReference("GeoLocation");
			description = buildDescription("Geolocation", field, parentType);
			break;
		case STRING:
		default:
			fetcher = new StringFieldDataFetcher(field.getName(), adaptors);
			graphQlType = GraphQLString;
			description = buildDescription("Textual field", field, parentType);
			break;
		}
		if (field.isArray()) {
			builder.type(list(graphQlType))
				.dataFetcher(fetcher);
		} else {
			builder.type(graphQlType)
				.dataFetcher(fetcher);
		}
		builder.description(description);
		return builder.build();
	}

	private String buildDescription(String prefix, Field field, String parentType) {
		return prefix + (field.isArray() ? "s '" : " '") + field.getName() + "' inside " + parentType;
	}

	private GraphQLFieldDefinition buildFieldDefinition(ReferenceField field, String parentType, Adaptors adaptors) {
		final Builder builder = newFieldDefinition().name(field.getName());
		final GraphQLOutputType graphQlType = new GraphQLTypeReference(field.getType());
		if (field.isArray()) {
			builder.type(list(graphQlType))
				.dataFetcher(new SubTypeArrayFetcher(field.getName(), adaptors));
		} else {
			builder.type(graphQlType)
				.dataFetcher(new SubTypeFetcher(field.getName(), adaptors));
		}
		builder.description(
				"Relational field" + (field.isArray() ? "s '" : " '") + field.getName() + "' referencing type " + field.getType() + " inside " + parentType);
		return builder.build();
	}

	private GraphQLOutputType buildDataTypeInterfaceType(Adaptors adaptors) {
		final GraphQLInterfaceType.Builder builder = newInterface().name("DataType")
			.description("General interface for the different data types (from the set " + buildPresentationListFromCollection(adaptors.getDataTypes()) + ")")
			.typeResolver(new DataTypeResolver(adaptors));
		builder.field(createUidField(adaptors));
		builder.field(createTypeField(adaptors));
		builder.field(createHeadlineField(adaptors));
		builder.field(createHtmlField(adaptors));
		return builder.build();
	}

	private GraphQLOutputType buildDocumentInterfaceType(Adaptors adaptors) {
		final GraphQLInterfaceType.Builder builder = newInterface().name("Document")
			.description(
					"Interface for the different types of documents (from the set " + buildPresentationListFromCollection(adaptors.getDocumentTypes()) + ")")
			.typeResolver(new DocumentResolver(adaptors));
		builder.field(createReferencesField(adaptors));
		builder.field(createTimeOfEventField(adaptors));
		builder.field(createTimeOfInterceptionField(adaptors));
		builder.field(createFormattedTimeOfEventField(adaptors));
		builder.field(createFormattedTimeOfInterceptionField(adaptors));
		builder.field(createUsageDataItemsField(adaptors));
		builder.field(createRawDataField(adaptors));
		return builder.build();
	}

	private GraphQLOutputType buildSelectorInterfaceType(Adaptors adaptors) {
		final GraphQLInterfaceType.Builder builder = newInterface().name("Selector")
			.description(
					"Interface for the different types of documents (from the set " + buildPresentationListFromCollection(adaptors.getDocumentTypes()) + ")")
			.typeResolver(new SelectorResolver(adaptors));
		builder.field(createStatisticsField(adaptors));
		builder.field(createKnowledgeField(adaptors));
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

	private GraphQLObjectType buildKnowledgeType(Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = newObject().name("Knowledge")
			.description("Existing knowledge about this selector");
		builder.field(newFieldDefinition().name("alias")
			.description("Alternative name for the selector")
			.type(GraphQLString)
			.dataFetcher(new SelectorAliasFetcher(adaptors))
			.build());
		builder.field(newFieldDefinition().name("isKnown")
			.description("How well do we know this selector")
			.type(new GraphQLTypeReference("ConfidenceLevelEnum"))
			.dataFetcher(new SelectorIsKnownFetcher(adaptors))
			.build());
		builder.field(newFieldDefinition().name("isRestricted")
			.description("Whether queries for this selector have to be justified")
			.type(new GraphQLTypeReference("ConfidenceLevelEnum"))
			.dataFetcher(new SelectorIsRestrictedFetcher(adaptors))
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

	private GraphQLOutputType buildUsageActivityItemType(Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = newObject().name("UsageActivityItem")
			.description("Activity by specific user on a specific document at a specific point in time");
		builder.field(newFieldDefinition().name("user")
			.description("Identity of the user who executed the action")
			.type(GraphQLString)
			.dataFetcher(environment -> ((UsageActivityItem) environment.getSource()).user)
			.build());
		builder.field(newFieldDefinition().name("activityType")
			.description("The activity type in question")
			.type(list(new GraphQLTypeReference("UsageActivityTypeEnum")))
			.dataFetcher(environment -> ((UsageActivityItem) environment.getSource()).type)
			.build());
		builder.field(newFieldDefinition().name("timeOfActivity")
			.description("The exact time for when the activity occurred, in milliseconds since the epoch")
			.type(GraphQLLong)
			.dataFetcher(environment -> ((UsageActivityItem) environment.getSource()).timeOfActivity)
			.build());
		builder.field(newFieldDefinition().name("formattedTimeOfActivity")
			.description("The exact time for when the activity occurred, in IEC 8601 format")
			.type(GraphQLLong)
			.dataFetcher(new FormattedTimeOfActivityFetcher(adaptors))
			.build());
		return builder.build();
	}

	private GraphQLOutputType buildGeoLocationType(Adaptors adaptors) {
		final GraphQLObjectType.Builder builder = newObject().name("GeoLocation")
			.description("Location in Longitude and Latitude");
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

	private GraphQLFieldDefinition createFormattedTimeOfInterceptionField(Adaptors adaptors) {
		return newFieldDefinition().name("formattedTimeOfInterception")
			.description("The exact time for when the event, defined by the document, was intercepted, in IEC 8601 format")
			.type(GraphQLString)
			.argument(format(new LinkedList<>()))
			.dataFetcher(new FormattedTimeOfInterceptionDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createFormattedTimeOfEventField(Adaptors adaptors) {
		return newFieldDefinition().name("formattedTimeOfEvent")
			.description("The best guess for when the event, defined by the document, occurred, in IEC 8601 format")
			.type(GraphQLString)
			.argument(format(new LinkedList<>()))
			.dataFetcher(new FormattedTimeOfEventDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createTimeOfInterceptionField(Adaptors adaptors) {
		return newFieldDefinition().name("timeOfInterception")
			.description("The exact time for when the event, defined by the document, was intercepted, in milliseconds since the epoch")
			.type(GraphQLLong)
			.dataFetcher(new TimeOfInterceptionDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createTimeOfEventField(Adaptors adaptors) {
		return newFieldDefinition().name("timeOfEvent")
			.description("The best guess for when the event, defined by the document, occurred, in milliseconds since the epoch")
			.type(GraphQLLong)
			.dataFetcher(new TimeOfEventDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createUsageDataItemsField(Adaptors adaptors) {
		return newFieldDefinition().name("usageActivities")
			.description("Registered user interaction with this document")
			.argument(pagingAnd(dateBoundsAnd(new LinkedList<>())))
			.type(list(new GraphQLTypeReference("UsageActivityItem")))
			.dataFetcher(new UsageActivityItemsFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createRawDataField(Adaptors adaptors) {
		return newFieldDefinition().name("rawData")
			.description("Raw representation of the document as stored in the index")
			.type(GraphQLString)
			.dataFetcher(new RawDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createReferencesField(Adaptors adaptors) {
		return newFieldDefinition().name("references")
			.description("References describing how the document was obtained and restrictions on its usage")
			.type(list(new GraphQLTypeReference("DocumentReference")))
			.dataFetcher(new DocumentReferencesFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createHtmlField(Adaptors adaptors) {
		return newFieldDefinition().name("html")
			.description("HTML representation of the entity")
			.type(GraphQLString)
			.dataFetcher(new HtmlDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createHeadlineField(Adaptors adaptors) {
		return newFieldDefinition().name("headline")
			.description("Simple textual representation of the entity")
			.type(GraphQLString)
			.dataFetcher(new HeadlineDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createTypeField(Adaptors adaptors) {
		return newFieldDefinition().name("type")
			.description("Data type of the entity")
			.type(GraphQLString)
			.dataFetcher(new TypeDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createUidField(Adaptors adaptors) {
		return newFieldDefinition().name("uid")
			.description("Unique id for entity")
			.type(GraphQLString)
			.dataFetcher(new UidDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createSimpleRepresentationField(Adaptors adaptors) {
		return newFieldDefinition().name("simpleRep")
			.description("Simple representation for the selector")
			.type(GraphQLString)
			.dataFetcher(new SimpleRepresentationDataFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createInvertedIndexLookupField(String dataType, Adaptors adaptors) {
		List<GraphQLArgument> arguments = pagingAnd(dateBoundsAnd(new LinkedList<>()));
		arguments = relationsAnd(arguments, adaptors.getIndexedRelationTypes(dataType));
		arguments = documentTypesAnd(arguments, adaptors.getIndexedDocumentTypes(dataType));
		arguments = justificationAnd(arguments);

		return newFieldDefinition().name("events")
			.description("Events that this \" + dataType + \" has been linked to in the index")
			.argument(arguments)
			.type(list(new GraphQLTypeReference("Document")))
			.dataFetcher(new DocumentListFetcher(adaptors))
			.build();

	}

	private GraphQLFieldDefinition createKnowledgeField(Adaptors adaptors) {
		return newFieldDefinition().name("knowledge")
			.description("Fetch prior knowledge about the selector from the analytics platform")
			.type(new GraphQLTypeReference("Knowledge"))
			.dataFetcher(new SelectorKnowledgeFetcher(adaptors))
			.build();
	}

	private GraphQLFieldDefinition createStatisticsField(Adaptors adaptors) {
		return newFieldDefinition().name("statistics")
			.description("Lookup statstics over how often the selector occurs in the index")
			.type(new GraphQLTypeReference("SelectorStatistics"))
			.argument(justificationAnd(new LinkedList<>()))
			.dataFetcher(new SelectorStatisticsFetcher(adaptors))
			.build();
	}

	private List<GraphQLArgument> uidAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("uid")
			.description("Unique id for entity")
			.type(GraphQLString)
			.build());
		return arguments;
	}

	private List<GraphQLArgument> simpleRepAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("simpleRep")
			.description("Simple representation for selector")
			.type(GraphQLString)
			.build());
		return arguments;
	}

	private List<GraphQLArgument> justificationAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("justification")
			.description("Justification for executing query")
			.type(GraphQLString)
			.build());
		arguments.add(newArgument().name("user")
			.description("ID of the user trying to execute the query")
			.type(nonNull(GraphQLString))
			.build());
		return arguments;
	}

	private List<GraphQLArgument> format(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("detailLevel")
			.description("Date rendering detail level; use 'dateOnly' to only render year-month-date or leave it out for date and time")
			.type(GraphQLString)
			.build());
		return arguments;
	}

	private List<GraphQLArgument> documentTypesAnd(List<GraphQLArgument> arguments, Collection<String> documentTypes) {
		final String documentTypeList = buildPresentationListFromCollection(documentTypes);
		arguments.add(newArgument().name("documentTypes")
			.description("Document types that should be included in the response (from the set " + documentTypeList + " )")
			.type(list(GraphQLString))
			.build());
		return arguments;
	}

	private List<GraphQLArgument> relationsAnd(List<GraphQLArgument> arguments, Collection<String> relationTypes) {
		final String relationTypeList = buildPresentationListFromCollection(relationTypes);
		arguments.add(newArgument().name("relations")
			.description("Relation types that should be included in the response (from the set " + relationTypeList + " )")
			.type(list(GraphQLString))
			.build());
		return arguments;
	}

	private List<GraphQLArgument> pagingAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("offset")
			.description("Index of first element in result to include")
			.type(GraphQLInt)
			.defaultValue(0)
			.build());
		arguments.add(newArgument().name("limit")
			.description("Number of elements from result to include")
			.type(GraphQLInt)
			.defaultValue(20)
			.build());
		return arguments;
	}

	private List<GraphQLArgument> dateBoundsAnd(List<GraphQLArgument> arguments) {
		arguments.add(newArgument().name("after")
			.description("Lower bound in milliseconds since the epoch")
			.type(GraphQLLong)
			.build());
		arguments.add(newArgument().name("before")
			.description("Upper bound in milliseconds since the epoch")
			.type(GraphQLLong)
			.build());
		arguments.add(newArgument().name("afterDate")
			.description("Lower bound formatted date in IEC 8601, e.g. '2017-11-23' or '2017-11-23T12:34:56+0200'")
			.type(GraphQLString)
			.build());
		arguments.add(newArgument().name("beforeDate")
			.description("Upper bound formatted date in IEC 8601, e.g. '2017-11-23' or '2017-11-23T12:34:56+0200'")
			.type(GraphQLString)
			.build());
		return arguments;
	}

	private String buildPresentationListFromCollection(Collection<String> values) {
		final String listOfValues = "[ " + values.stream()
			.sorted()
			.collect(joining(", ")) + " ]";
		return listOfValues;
	}
}
